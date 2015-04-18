package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.providers.SortableGameProvider;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.*;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * It contains all games in a pageable list, there are four possible ways to order
 * the list. Order alphabetically, Order by rating or order by amount of ratings, or
 * by amount of comments.
 */
public class ListGamePanel extends Panel {
    @SpringBean
    GameService gameService;
    @SpringBean
    LabelService labelService;
    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    RatingService ratingService;
    @SpringBean
    CommentService commentService;

    private SortableGameProvider sgp;

    private RatingsModel ratingsModel;
    private CommentsModel commentsModel;

    private class CommentsModel extends LoadableDetachableModel<List<Game>> {
        final int userId;

        private CommentsModel(int userId) {
            this.userId = userId;
        }

        @Override
        protected List<Game> load() {
            if(userId == 0) {
                return new ArrayList<Game>();
            } else {
                return new ArrayList<Game>(commentService.getGamesCommentedByUser(userId));
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
                return new ArrayList<Game>();
            } else {
                return new ArrayList<Game>(gameService.getGamesRatedByUser(userId));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public ListGamePanel(String id, int filterLabel) {
        super(id);

        cz.larpovadatabaze.entities.Label label;
        if(filterLabel != -1){
            label = labelService.getById(filterLabel);
        } else {
            label = null;
        }

        CsldUser loggedUser =  CsldAuthenticatedWebSession.get().getLoggedUser();
        if(loggedUser != null) {
            ratingsModel = new RatingsModel(loggedUser.getId());
            commentsModel = new CommentsModel(loggedUser.getId());
        } else {
            ratingsModel = new RatingsModel(0);
            commentsModel = new CommentsModel(0);
        }

        sgp = new SortableGameProvider(gameService, label, Session.get().getLocale());
        final DataView<Game> propertyList = new DataView<Game>("listGames", sgp) {
            @Override
            protected void populateItem(Item<Game> item) {
                Game game = item.getModelObject();
                int itemIndex = game.getFirst() + item.getIndex() + 1;
                final Label orderLabel = new Label("order", itemIndex);
                item.add(orderLabel);

                final BookmarkablePageLink<CsldBasePage> gameLink =
                        new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, GameDetail.paramsForGame(game));
                final Label nameLabel = new Label("gameName", Model.of(game.getName()));
                gameLink.add(nameLabel);
                item.add(gameLink);

                final Label gameYear = new Label("gameYear", Model.of(game.getYear()));
                item.add(gameYear);

                Long totalRating = Math.round(game.getTotalRating());
                DecimalFormat df = new DecimalFormat("0.0");
                final Label gameRating = new Label("rating", Model.of(df.format((double) totalRating / 10d)));
                item.add(gameRating);

                Long averageRating = (totalRating == 0)?0:Math.round(game.getAverageRating());
                final Label average = new Label("average", Model.of(df.format((double) averageRating / 10d)));
                item.add(average);

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

        add(new OrderByBorder("orderByName", "form.wholeName", sgp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged() {
                propertyList.setCurrentPage(0);
            }
        });

        add(new OrderByBorder("orderByYear", "year", sgp)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged()
            {
                propertyList.setCurrentPage(0);
            }
        });

        add(new OrderByBorder("orderByRating", "rating", sgp)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged()
            {
                propertyList.setCurrentPage(0);
            }
        });

        add(new OrderByBorder("orderByRatingAmount", "ratingAmount", sgp)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged()
            {
                propertyList.setCurrentPage(0);
            }
        });


        add(new OrderByBorder("orderByCommentAmount", "commentAmount", sgp)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged()
            {
                propertyList.setCurrentPage(0);
            }
        });


        add(propertyList);
        PagingNavigator paging = new PagingNavigator("navigator", propertyList);
        add(paging);
    }

    public void reload(AjaxRequestTarget target, FilterGame filterGame, List<cz.larpovadatabaze.entities.Label> labels) {
        sgp.setFilters(filterGame, labels);

        target.add(ListGamePanel.this);
    }
}