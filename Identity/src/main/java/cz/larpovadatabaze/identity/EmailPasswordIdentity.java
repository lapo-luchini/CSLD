package cz.larpovadatabaze.identity;

import org.hibernate.criterion.DetachedCriteria;

/**
 *
 */
public class EmailPasswordIdentity implements Identity {
    private String email;
    private String password;

    public EmailPasswordIdentity(String email, String password){
        this.email = email;
        this.password = password;
    }

    public boolean validate() {
        DetachedCriteria presenceByEmail = DetachedCriteria.forClass(User.class).
                createCriteria("email", email);
        return false;
    }

    public boolean store() {
        return false;
    }
}
