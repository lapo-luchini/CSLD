package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CommentService;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * It contains all games in a pageable list, there are four possible ways to order
 * the list. Order alphabetically, Order by rating or order by amount of ratings, or
 * by amount of comments.
 */
public abstract class AbstractListGamePanel<T> extends AbstractCsldPanel<T> {
    @SpringBean
    GameService gameService;
    @SpringBean
    CommentService commentService;

    private SortableDataProvider<Game, String> sdp;

    private RatingsModel ratingsModel;
    private CommentsModel commentsModel;

    public AbstractListGamePanel(String id, IModel<T> model) {
        super(id, model);
    }

    /**
     * @return Data provider
     */
    protected abstract SortableDataProvider<Game, String> getDataProvider();

    @Override
    protected void onInitialize() {
        super.onInitialize();

        CsldUser loggedUser =  CsldAuthenticatedWebSession.get().getLoggedUser();
        if(loggedUser != null) {
            ratingsModel = new RatingsModel(loggedUser.getId());
            commentsModel = new CommentsModel(loggedUser.getId());
        } else {
            ratingsModel = new RatingsModel(0);
            commentsModel = new CommentsModel(0);
        }

        sdp = getDataProvider();
        final DataView<Game> propertyList = new DataView<Game>("listGames", sdp) {
            @Override
            protected void populateItem(Item<Game> item) {
                Game game = item.getModelObject();
                int itemIndex = game.getFirst() + item.getIndex() + 1;

                item.add(new GameNameAndLabelsPanel("nameAndLabels", item.getModel()));

                final Label gameYear = new Label("gameYear", Model.of(game.getYear()));
                item.add(gameYear);

                item.add(new GameRatingBoxWithAveragePanel("ratingBox", item.getModel()));

                final Label gameRatings = new Label("ratings", game.getAmountOfRatings());
                if(ratingsModel.getObject().contains(game)){
                    gameRatings.add(new AttributeAppender("class"," rated"));
                }
                item.add(gameRatings);

                final Label gameComments = new Label("comments", game.getAmountOfComments());
                if(commentsModel.getObject().contains(game)){
                    gameComments.add(new AttributeAppender("class"," commented"));
                }
                item.add(gameComments);
            }
        };
        propertyList.setOutputMarkupId(true);
        propertyList.setItemsPerPage(25L);

        add(propertyList);
        PagingNavigator paging = new PagingNavigator("navigator", propertyList);
        add(paging);
    }

    private class CommentsModel extends LoadableDetachableModel<List<Game>> {
        final int userId;

        private CommentsModel(int userId) {
            this.userId = userId;
        }

        @Override
        protected List<Game> load() {
            if(userId == 0) {
                return new ArrayList<>();
            } else {
                return new ArrayList<>(commentService.getGamesCommentedByUser(userId));
            }
        }
    }

    private class RatingsModel extends LoadableDetachableModel<List<Game>> {
        final int userId;

        private RatingsModel(int userId) {
            this.userId = userId;
        }

        @Override
        protected List<Game> load() {
            if(userId == 0) {
                return new ArrayList<>();
            } else {
                return new ArrayList<>(gameService.getGamesRatedByUser(userId));
            }
        }
    }
}
