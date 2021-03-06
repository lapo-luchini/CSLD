package cz.larpovadatabaze.components.panel.home;

import cz.larpovadatabaze.behavior.dotdotdot.DotDotDotBehavior;
import cz.larpovadatabaze.components.common.icons.UserIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.page.user.UserDetailPage;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.CommentService;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.jsoup.Jsoup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This panel shows shortened info about last comments.
 */
public class LastCommentsPanel extends Panel {
    /** Number of columns */
    private static final int N_COLUMNS = 3;

    @SpringBean
    CommentService commentService;

    @SpringBean
    ImageService imageService;

    private static final int MAX_CHARS_IN_COMMENT = 300;
    private static int INITIAL_LAST_COMMENTS = N_COLUMNS*2;
    private static int EXPANDED_LAST_COMMENTS = N_COLUMNS*5;

    private class CommentsView extends ListView<Comment> {
        public CommentsView(String id, List<? extends Comment> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(final ListItem<Comment> item) {
            Comment comment = item.getModelObject();
            Game game = comment.getGame();
            CsldUser commenter = comment.getUser();

            // Create fragment
            Fragment f = new Fragment("comment", "commentFragment", LastCommentsPanel.this);
            item.add(f);

            // Create game parameters
            PageParameters gameParams = GameDetail.paramsForGame(game);

            // Comment wrapper
            WebMarkupContainer commentWrapper = new WebMarkupContainer("commentWrapper");
            f.add(commentWrapper);

            // "More" link after the comment
            BookmarkablePageLink moreLink = new BookmarkablePageLink("moreLink", GameDetail.class, gameParams);
            commentWrapper.add(moreLink);

            // Content
            String commentToShow = Jsoup.parse(comment.getComment()).text();
            if(commentToShow.length() > MAX_CHARS_IN_COMMENT){
                commentToShow = commentToShow.substring(0,MAX_CHARS_IN_COMMENT);
            }
            Label commentsContent = new Label("commentsContent", Model.of(commentToShow));
            commentsContent.setEscapeModelStrings(false);
            commentWrapper.add(commentsContent);
            commentWrapper.add(new DotDotDotBehavior().setAfterComponentId(moreLink.getMarkupId(true)));

            // User icon
            final UserIcon commenterIcon = new UserIcon("commenterIcon", new AbstractReadOnlyModel<CsldUser>() {
                @Override
                public CsldUser getObject() {
                    return item.getModelObject().getUser();
                }
            });
            f.add(commenterIcon);

            // Link and name
            PageParameters userParams = new PageParameters();
            userParams.add("id", commenter.getId());
            final BookmarkablePageLink<CsldBasePage> commenterLink =
                new BookmarkablePageLink<CsldBasePage>("commenterLink", UserDetailPage.class, userParams);
            Label commenterName = new Label("commenterName", Model.of(commenter.getPerson().getNickNameView()));
            commenterLink.add(commenterName);
            f.add(commenterLink);

            // Game link
            final BookmarkablePageLink<CsldBasePage> gameLink = new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, gameParams);
            f.add(gameLink);

            // Game rating
            String gameRatingColor = Rating.getColorOf(game.getAverageRating());
            Label gameRating = new Label("gameRating","");
            gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
            gameLink.add(gameRating);

            // Game name
            final Label gameName = new Label("gameName", game.getName());
            gameLink.add(gameName);

            // Game date
            SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
            Date dateOfComment = new Date();
            dateOfComment.setTime(comment.getAdded().getTime());
            Label commentDate = new Label("commentDate", Model.of(formatDate.format(dateOfComment)));
            f.add(commentDate);
        }
    }

    public LastCommentsPanel(String id) {
        super(id);
    }

    /**
     * Shuffle list to columns
     *
     * @param list Original list
     *
     * @return Shuffled list
     */
    private List<Comment> toColumns(List<Comment> list) {
        List<Comment> res = new ArrayList<>();
        int colLen = (list.size()+ N_COLUMNS -1)/3;
        for(int i=0; i<colLen; i++) {
            for(int n=0; n< N_COLUMNS; n++) {
                int idx = n*colLen + i;
                if (idx < list.size()) {
                    res.add(list.get(idx));
                }
            }
        }

        return res;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        List<Comment> toShow = new ArrayList<Comment>(commentService.getLastComments(EXPANDED_LAST_COMMENTS));

        if(toShow.size() >= INITIAL_LAST_COMMENTS) {
            add(new CommentsView("visibleComments", toColumns(toShow.subList(0, INITIAL_LAST_COMMENTS))));
            add(new CommentsView("hiddenComments", toColumns(toShow.subList(0, toShow.size()))));
        } else {
            add(new CommentsView("visibleComments", toColumns(toShow)));
            add(new CommentsView("hiddenComments", toColumns(toShow)));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(OnDomReadyHeaderItem.forScript(new PackageTextTemplate(getClass(), "LastCommentsPanel.js").getString()));
    }
}
