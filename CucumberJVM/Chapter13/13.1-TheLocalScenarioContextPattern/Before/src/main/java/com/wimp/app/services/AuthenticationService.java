package com.wimp.app.services;

import com.wimp.app.infrastructure.UserSession;

public class AuthenticationService {
    public static void login(String customerName) {
        UserSession.getCurrent().setLoggedInCustomerName(customerName);
    }

    public static boolean isLoggedIn(String customerName) {
        return customerName.equals(UserSession.getCurrent().getLoggedInCustomerName());
    }
}
