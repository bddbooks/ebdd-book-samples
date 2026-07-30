package com.wimp.specs.stepdefinitions;

import com.wimp.specs.drivers.AuthenticationApiDriver;
import com.wimp.specs.support.DomainDefaults;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthenticationStepDefinitions {
    private final AuthenticationApiDriver authenticationApiDriver;

    @Autowired
    public AuthenticationStepDefinitions(AuthenticationApiDriver authenticationApiDriver) {
        this.authenticationApiDriver = authenticationApiDriver;
    }

    @Given("the customer is authenticated")
    public void theCustomerIsAuthenticated() throws Exception {
        authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.PASSWORD).execute();
    }
}
