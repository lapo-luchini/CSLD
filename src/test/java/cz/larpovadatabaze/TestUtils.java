package cz.larpovadatabaze;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Simple Utility methods useful for testing.
 */
public class TestUtils {
    public static void logUser(CsldUser toLog) {
        ReflectionTestUtils.setField(CsldAuthenticatedWebSession.get(), "csldUser", toLog);
        if(toLog != null) {
            ReflectionTestUtils.setField(CsldAuthenticatedWebSession.get(), "signedIn", true);
        }
    }

    public static void logoutUser() {
        ReflectionTestUtils.setField(CsldAuthenticatedWebSession.get(), "csldUser", null);
        ReflectionTestUtils.setField(CsldAuthenticatedWebSession.get(), "signedIn", false);
    }
}
