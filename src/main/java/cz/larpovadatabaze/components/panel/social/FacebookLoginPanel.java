package cz.larpovadatabaze.components.panel.social;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Person;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Balda on 18. 7. 2015.
 */
public class FacebookLoginPanel extends Panel {
    private WebMarkupContainer fbloginDiv;
    private Label fblogin;

    public FacebookLoginPanel(String id) {
        super(id);
    }

    /**
     * This method will the panel
     */
    public void createPanel() {
        fbloginDiv = new WebMarkupContainer("fbloginDiv");
        fbloginDiv.setOutputMarkupId(true).setMarkupId("fbloginDiv");
        fblogin = new Label("fblogin", "<fb:login-button onlogin='callWicket();'></fb:login-button>");
        fblogin.setEscapeModelStrings(false);
        fblogin.setOutputMarkupId(true);
        if (isAuthenticated()) {
            fbloginDiv.add(new SimpleAttributeModifier("style", "display:none;"));
        }
        fbloginDiv.add(fblogin);
        addOrReplace(fbloginDiv);
        /**
         * This will only be called after they're logged in via facebook
         */
        final AbstractDefaultAjaxBehavior behave = new AbstractDefaultAjaxBehavior() {
            protected void respond(final AjaxRequestTarget target) {
                // deal with facebook
                handleFacebookCallback(target.getPage());
                fbloginDiv.add(new SimpleAttributeModifier("style", "display:none;"));
                target.add(fbloginDiv);
            }
        };
        add(behave);
        CharSequence url = behave.getCallbackUrl();
        StringBuffer sb = new StringBuffer();
        sb.append("function callWicket() { \n");
        sb.append("     var wcall = wicketAjaxGet('");
        sb.append(url);
        sb.append("', function() { }, function() { });");
        sb.append("    }");
        Label fbcallback = new Label("fbcallback", sb.toString());
        fbcallback.setOutputMarkupId(true);
        fbcallback.setEscapeModelStrings(false);
        add(fbcallback);

    }

    /**
     * All that we do to log you in from facebook. I put my fbook.key and fbook.secret in the
     * properties file.
     * @param thePage
     */
    public void handleFacebookCallback(Page thePage) {

        HttpServletRequest req = ((ServletWebRequest) thePage.getRequest()).getContainerRequest();
        HttpServletResponse res = (HttpServletResponse) thePage.getResponse().getContainerResponse();
        String api = getLocalizer().getString("fbook.key", this);
        String secret = getLocalizer().getString("fbook.secret", this);
        FacebookWebappHelper<Object> helper = FacebookWebappHelper.newInstanceJson(req, res, api, secret);

        // make sure the login worked
        if (helper.isLogin()) {
            FacebookJsonRestClient facebookClient = (FacebookJsonRestClient) helper.getFacebookRestClient();
            long id;
            try {
                // grab the logged in user's id
                id = facebookClient.users_getLoggedInUser();

                // you can bundle ajax calls...
                facebookClient.beginBatch();

                // i'm going to call the users.getInfo fb api call, just to make sure it works
                ArrayList<Long> ids = new ArrayList<Long>();
                ids.add(new Long(id));

                // put together a set of fields for fb to return
                HashSet<ProfileField> fields = new HashSet<ProfileField>();
                fields.add(ProfileField.FIRST_NAME);
                fields.add(ProfileField.LAST_NAME);

                // get the user data
                facebookClient.users_getInfo(ids, fields);

                // execute the batch (which also terminates batch mode until beginBatch is called again)
                List<? extends Object> batchResponse = facebookClient.executeBatch(false);
                JSONArray userInfo = (JSONArray) batchResponse.get(0);
                JSONObject user = userInfo.getJSONObject(0);

                // a pojo user object
                CsldUser theUser = new CsldUser();
                Person userPersonalInfo = new Person();
                String username = user.getString("first_name");
                userPersonalInfo.setName(username);

                // fb emails are proxy, my app needs some kind of holder
                userPersonalInfo.setEmail("noreply@facebook.com");
                theUser.setFacebook(true);
                theUser.setFacebookId(id);

            } catch (FacebookException e) {
                log.error("facebook issues: " + e);
            } catch (JSONException e) {
                log.error("facebook json issues: " + e);
            }
        }
    }

    /**
     * Do your own kind of auth check
     * @return
     */
    public boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }
}
