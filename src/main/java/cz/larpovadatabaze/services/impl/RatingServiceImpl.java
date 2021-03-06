package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.RatingDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.RatingService;
import cz.larpovadatabaze.services.UserPlayedGameService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Repository
@Transactional
public class RatingServiceImpl implements RatingService {
    @Autowired
    private RatingDAO ratingDAO;
    @Autowired
    private CsldUserService csldUserService;

    @Autowired
    private UserPlayedGameService userPlayedGameService;

    @Autowired
    private GameService gameService;

    public Rating getUserRatingOfGame(Integer userId, Integer gameId)
    {
        Criterion[] criterions = new Criterion[1];
        criterions[0] = Restrictions.conjunction()
                .add(Restrictions.eq("game.id", gameId))
                .add(Restrictions.eq("user.id", userId));
        return ratingDAO.findSingleByCriteria(criterions);
    }

    @Override
    public List<Rating> getAll()
    {
        List<Rating> ratings = ratingDAO.findAll();
        ratingDAO.flush();
        return ratings;
    }

    @Override
    public void remove(Rating toRemove) {
        ratingDAO.makeTransient(toRemove);

        // Some fields in the game object are computed by triggers - flush corresponding game from hibernate cache so it is reloaded
        gameService.evictGame(toRemove.getGame().getId());
    }

    @Override
    public List<Rating> getFirstChoices(String startsWith, int maxChoices) {
        throw new UnsupportedOperationException("This does not support autocompletion");
    }

    @Override
    public List<Rating> getUnique(Rating example) {
        return ratingDAO.findByExample(example, new String[]{});
    }

    @Override
    public double getAverageRating() {
        return ratingDAO.getAverageRating();
    }

    @Override
    public void saveOrUpdate(Rating actualRating) {
        actualRating.setAdded(new Timestamp(new Date().getTime()));
        ratingDAO.saveOrUpdate(actualRating);

        // Mark that user played game
        UserPlayedGame upg = userPlayedGameService.getUserPlayedGame(actualRating.getGame().getId(), actualRating.getUser().getId());
        if(upg == null){
            upg = new UserPlayedGame();
            upg.setGame(actualRating.getGame());
            upg.setStateEnum(UserPlayedGame.UserPlayedGameState.PLAYED);
            upg.setPlayerOfGame(actualRating.getUser());
        } else {
            if(upg.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.NONE)){
                upg.setStateEnum(UserPlayedGame.UserPlayedGameState.PLAYED);
            }
        }
        userPlayedGameService.saveOrUpdate(upg);

        // Some fields in the game object are computed by triggers - flush corresponding game from hibernate cache so it is reloaded
        gameService.evictGame(actualRating.getGame().getId());
    }

    @Override
    public List<Rating> getRatingsOfUser(CsldUser logged, CsldUser actual) {
        if(logged == null || (!logged.getId().equals(actual.getId()) && !csldUserService.isLoggedAtLeastEditor())) {
            return new ArrayList<>();
        } else {
            return ratingDAO.getRatingsOfUser(actual.getId());
        }
    }

    @Override
    public void delete(Rating rating) {
        ratingDAO.makeTransient(rating);

        gameService.evictGame(rating.getGame().getId());
    }
}
