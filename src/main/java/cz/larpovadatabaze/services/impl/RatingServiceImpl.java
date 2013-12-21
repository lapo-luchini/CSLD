package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.RatingDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.RatingService;
import cz.larpovadatabaze.services.UserPlayedGameService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 9.4.13
 * Time: 11:27
 */
@Repository
public class RatingServiceImpl implements RatingService {
    @Autowired
    private RatingDAO ratingDAO;

    @Autowired
    private UserPlayedGameService userPlayedGameService;

    @Autowired
    private GameService gameService;

    public Rating getUserRatingOfGame(Integer userId, Integer gameId)
    {
        Criterion[] criterions = new Criterion[1];
        criterions[0] = Restrictions.conjunction()
                .add(Restrictions.eq("gameId", gameId))
                .add(Restrictions.eq("userId", userId));
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
        gameService.evictGame(toRemove.getGameId());
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
        ratingDAO.saveOrUpdate(actualRating);

        // Mark that user played game
        UserPlayedGame upg = userPlayedGameService.getUserPlayedGame(actualRating.getGameId(), actualRating.getUserId());
        if(upg == null){
            upg = new UserPlayedGame();
            upg.setGameId(actualRating.getGameId());
            upg.setStateEnum(UserPlayedGame.UserPlayedGameState.PLAYED);
            upg.setUserId(actualRating.getUserId());
        } else {
            if(upg.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.NONE)){
                upg.setStateEnum(UserPlayedGame.UserPlayedGameState.PLAYED);
            }
        }
        userPlayedGameService.saveOrUpdate(upg);

        // Some fields in the game object are computed by triggers - flush corresponding game from hibernate cache so it is reloaded
        gameService.evictGame(actualRating.getGameId());
    }

    @Override
    public int getAmountOfRatings() {
        return ratingDAO.getAmountOfRatings();
    }

    @Override
    public Integer getRatingsForGame(Integer id) {
        return ratingDAO.getRatingsForGame(id);
    }

    @Override
    public List<Rating> getRatingsOfUser(CsldUser logged, CsldUser actual) {
        if(logged == null || !logged.getId().equals(actual.getId())) {
            return new ArrayList<Rating>();
        } else {
            return ratingDAO.getRatingsOfUser(logged.getId());
        }
    }
}
