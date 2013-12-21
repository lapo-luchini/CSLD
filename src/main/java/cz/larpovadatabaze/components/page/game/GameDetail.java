package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.*;
import cz.larpovadatabaze.components.panel.user.SimpleListUsersPanel;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.models.ReadOnlyModel;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class GameDetail extends CsldBasePage {
    @SpringBean
    GameService gameService;

    private RatingsResultPanel ratingsResult;
    private RatingsPanel ratingsPanel;

    private GameModel model;

    /**
     * Model for game specified by game id
     */
    private class GameModel extends LoadableDetachableModel<Game> {

        // Game id. We could also store id as page property.
        private int gameId;

        private GameModel(int gameId) {
            this.gameId = gameId;
        }

        @Override
        protected Game load() {
            Game game = gameService.getById(gameId);
            if(HbUtils.isProxy(game)){
                game = HbUtils.deproxy(game);
            }

            /**
             * Ratings and comments are set to lazy load, but they can be only loaded in the same request as the Game object is loaded. So when Game
             * object is loaded and comments are not touched (when updating ratings, for example) and in the next request we need to show comments,
             * we get an exception.
             *
             * To overcome this, we "touch" comments and ratings on load of Game objects to ensure they are loaded. We could also change load from lazy to eager,
             * but that would affect other places when game is loaded and comments/ratings are not needed.
             */
            game.getComments().size();
            game.getRatings().size();
            game.getAuthors().size();

            return game;
        }
    }

    /**
     * Model for comments of actual game. (Might get GameModel as constructor parameter to be extra clean, but we use the one stored in the page.)
     * The downside is it does not cache results so getObject() may be costly.
     */
    private class CommentsModel extends ReadOnlyModel<List<Comment>> {
        @Override
        public List<Comment> getObject() {
            List<Comment> res = new ArrayList<Comment>(model.getObject().getComments());
            Collections.sort(res, new Comparator<Comment>() {
                @Override
                public int compare(Comment o1, Comment o2) {
                    return -o1.getAdded().compareTo(o2.getAdded());
                }
            });

            return res;
        }
    }

    /**
     * Model for users who want to play the game. (Might get GameModel as constructor parameter to be extra clean, but we use the one stored in the page.)
     * The downside is it does not cache results so getObject() may be costly.
     */
    private class WantedByModel extends ReadOnlyModel<List<CsldUser>> {

        @Override
        public List<CsldUser> getObject() {
            List<CsldUser> wantedBy = new ArrayList<CsldUser>();
            for(UserPlayedGame played : model.getObject().getPlayed()){
                if(played.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.WANT_TO_PLAY)) {
                    wantedBy.add(played.getPlayerOfGame());
                }
            }

            return wantedBy;
        }
    }

    /**
     * Constructor - initiaize just model
     */
    public GameDetail(PageParameters params){
        model = new GameModel(params.get("id").to(Integer.class));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final CommentsListPanel comments = new CommentsListPanel("commentsList", new CommentsModel());
        comments.setOutputMarkupId(true);

        final SimpleListUsersPanel wantedToPlay =  new SimpleListUsersPanel("wantsToPlay", new WantedByModel());
        wantedToPlay.setOutputMarkupId(true);

        add(new GameDetailPanel("gameDetail", model));
        add(new CommentsPanel("addComment", model, new Component[] { comments }));
        add(comments);

        WebMarkupContainer ratingsContainerPanel = new WebMarkupContainer("ratingsContainerPanel");
        ratingsContainerPanel.setOutputMarkupId(true);

        ratingsResult = new RatingsResultPanel("ratingsResults", model, ratingsContainerPanel, wantedToPlay);
        ratingsResult.setOutputMarkupId(true);
        ratingsContainerPanel.add(ratingsResult);

        ratingsPanel = new RatingsPanel("ratingsPanel", model, ratingsContainerPanel);
        ratingsContainerPanel.add(ratingsPanel);

        ratingsContainerPanel.add(new CanNotRatePanel("canNotRatePanel"));

        add(ratingsContainerPanel);

        EditGamePanel editGamePanel = new EditGamePanel("editGamePanel", model);
        add(editGamePanel);

        DeleteGamePanel deleteGamePanel = new DeleteGamePanel("deleteGamePanel", model.gameId);
        add(deleteGamePanel);

        add(new GameListPanel("similarGames", new LoadableDetachableModel<List<? extends Game>>() {
            @Override
            protected List<Game> load() {
                return gameService.getSimilar(model.getObject());
            }
        }));

        add(new GameListPanel("gamesOfAuthors", new LoadableDetachableModel<List<? extends Game>>() {
            @Override
            protected List<Game> load() {
                return gameService.gamesOfAuthors(model.getObject());
            }
        }));

        add(wantedToPlay);
    }
}
