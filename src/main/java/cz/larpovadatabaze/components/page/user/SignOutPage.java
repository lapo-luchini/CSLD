package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.ListGamePage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 9.4.13
 * Time: 9:43
 */
public class SignOutPage extends CsldBasePage {
    public SignOutPage(final PageParameters parameters)
    {
        CsldAuthenticatedWebSession.get().signOut();
        IAuthenticationStrategy strategy = getApplication().getSecuritySettings()
                .getAuthenticationStrategy();
        strategy.remove();

        final BookmarkablePageLink<CsldUser> homePageLink =
                new BookmarkablePageLink<CsldUser>("homePageLink", ListGamePage.class);
        add(homePageLink);
    }
}
