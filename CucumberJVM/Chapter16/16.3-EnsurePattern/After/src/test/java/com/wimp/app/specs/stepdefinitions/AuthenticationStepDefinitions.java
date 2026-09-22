package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.specs.drivers.AuthenticationApiDriver;
import com.wimp.app.specs.support.AuthenticationContext;
import com.wimp.app.specs.support.DomainDefaults;
import io.cucumber.java.en.*;

public class AuthenticationStepDefinitions {
    private final AuthenticationContext authenticationContext;
    private final AuthenticationApiDriver authenticationApiDriver;

    public AuthenticationStepDefinitions(AuthenticationContext authenticationContext, AuthenticationApiDriver authenticationApiDriver) {
        this.authenticationContext = authenticationContext;
        this.authenticationApiDriver = authenticationApiDriver;
    }

    @Given("the customer is authenticated")
    public void theCustomerIsAuthenticated() throws Exception {
        authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.PASSWORD).execute();
        authenticationContext.setAuthenticatedCustomerName(DomainDefaults.CUSTOMER_NAME);
    }
}
