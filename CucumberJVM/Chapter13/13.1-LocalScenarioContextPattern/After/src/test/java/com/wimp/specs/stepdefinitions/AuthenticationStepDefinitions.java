package com.wimp.specs.stepdefinitions;

import com.wimp.app.services.AuthenticationService;
import io.cucumber.java.en.Given;

public class AuthenticationStepDefinitions {
    @Given("the customer {word} is authenticated")
    public void theCustomerIsAuthenticated(String customerName) {
        AuthenticationService.login(customerName);
    }
}
