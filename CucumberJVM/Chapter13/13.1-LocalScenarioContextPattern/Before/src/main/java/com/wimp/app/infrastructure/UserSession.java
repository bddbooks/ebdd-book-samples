/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

package com.wimp.app.infrastructure;

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

    public String getAuthenticatedCustomerName() {
        return loggedInCustomerName;
    }

    public void setAuthenticatedCustomerName(String loggedInCustomerName) {
        this.loggedInCustomerName = loggedInCustomerName;
    }
}
