package cz.larpovadatabaze.identity;

/**
 * Created by jbalhar on 3/30/2015.
 */
public class SocialIdentity implements Identity {
    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public boolean store() {
        return false;
    }
}
