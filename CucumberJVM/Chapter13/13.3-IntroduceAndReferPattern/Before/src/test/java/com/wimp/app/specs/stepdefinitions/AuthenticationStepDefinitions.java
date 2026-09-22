package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.services.AuthenticationService;
import com.wimp.app.specs.support.AuthenticationContext;
import io.cucumber.java.en.*;

public class AuthenticationStepDefinitions {
    private final AuthenticationContext authenticationContext;

    public AuthenticationStepDefinitions(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    @Given("the customer {string} is authenticated")
    public void theCustomerIsAuthenticated(String customerName) {
        AuthenticationService.login(customerName);
        authenticationContext.setAuthenticatedCustomerName(customerName);
    }
}
