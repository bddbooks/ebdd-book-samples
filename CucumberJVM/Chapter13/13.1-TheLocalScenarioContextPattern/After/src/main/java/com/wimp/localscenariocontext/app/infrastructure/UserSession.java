package com.wimp.localscenariocontext.app.infrastructure;

/**
 * A simple simulation of a user session, using ThreadLocal to store the current session
 * for the current thread. In a real application, this would likely be more complex
 * and involve actual user authentication and session management.
 */
public class UserSession {
    private static final ThreadLocal<UserSession> current = ThreadLocal.withInitial(UserSession::new);

    public static UserSession getCurrent() {
        return current.get();
    }

    private String loggedInCustomerName;

    public String getLoggedInCustomerName() {
        return loggedInCustomerName;
    }

    public void setLoggedInCustomerName(String loggedInCustomerName) {
        this.loggedInCustomerName = loggedInCustomerName;
    }
}
