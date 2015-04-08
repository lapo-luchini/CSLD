package cz.larpovadatabaze.identity;

/**
 * Every other type of identity actually can wrap identity.
 */
public interface Identity {
    /**
     * This method should validate current identity. In case the identity doesn't exist it returns false
     *
     * @return true if this instance of identity does exist.
     */
    boolean validate();

    /**
     * This method stores the identity in any of available storages. Typical storage is database, but definitely it
     * isn't necessary.
     *
     * @return true if the identity was correctly stored.
     */
    boolean store();
}
