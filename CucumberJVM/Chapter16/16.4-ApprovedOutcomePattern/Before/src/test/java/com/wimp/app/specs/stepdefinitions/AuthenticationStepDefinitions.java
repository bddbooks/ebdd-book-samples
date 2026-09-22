package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.specs.drivers.AuthenticationApiDriver;
import com.wimp.app.specs.support.DomainDefaults;
import io.cucumber.java.en.*;

public class AuthenticationStepDefinitions {
    private final AuthenticationApiDriver authenticationApiDriver;

    public AuthenticationStepDefinitions(AuthenticationApiDriver authenticationApiDriver) {
        this.authenticationApiDriver = authenticationApiDriver;
    }

    @Given("the restaurant owner is authenticated")
    public void theRestaurantOwnerIsAuthenticated() throws Exception {
        authenticationApiDriver.login(DomainDefaults.RESTAURANT_OWNER, DomainDefaults.PASSWORD).execute();
    }
}
