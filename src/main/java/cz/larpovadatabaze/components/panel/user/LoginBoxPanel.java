package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.CreateUserPage;
import cz.larpovadatabaze.components.page.user.CsldSignInPage;
import cz.larpovadatabaze.entities.CsldUser;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 * It links for login/registration or user info
 */
public class LoginBoxPanel extends AbstractCsldPanel<CsldUser> {

    public LoginBoxPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Add login / logout links
        add(new BookmarkablePageLink<CsldBasePage>("login2", CsldSignInPage.class));
        add(new BookmarkablePageLink<CsldBasePage>("login3", CsldSignInPage.class));
        add(new BookmarkablePageLink<CsldBasePage>("register2", CreateUserPage.class));
        add(new BookmarkablePageLink<CsldBasePage>("register3", CreateUserPage.class));

    }
}
