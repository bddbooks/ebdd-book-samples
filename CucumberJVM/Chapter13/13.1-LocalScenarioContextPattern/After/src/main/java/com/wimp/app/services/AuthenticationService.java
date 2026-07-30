package com.wimp.app.services;

import com.wimp.app.infrastructure.UserSession;

public class AuthenticationService {
    public static void login(String customerName) {
        UserSession.getCurrent().setAuthenticatedCustomerName(customerName);
    }

    public static boolean isAuthenticated(String customerName) {
        return customerName.equals(UserSession.getCurrent().getAuthenticatedCustomerName());
    }
}
