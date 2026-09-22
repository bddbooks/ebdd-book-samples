package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.specs.drivers.AuthenticationApiDriver;
import com.wimp.app.specs.support.DomainDefaults;
import io.cucumber.java.en.*;

public class AuthenticationStepDefinitions {
    private final AuthenticationApiDriver authenticationApiDriver;

    public AuthenticationStepDefinitions(AuthenticationApiDriver authenticationApiDriver) {
        this.authenticationApiDriver = authenticationApiDriver;
    }

    @Given("the customer is authenticated")
    public void theCustomerIsAuthenticated() throws Exception {
        authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.PASSWORD).execute();
    }
}
