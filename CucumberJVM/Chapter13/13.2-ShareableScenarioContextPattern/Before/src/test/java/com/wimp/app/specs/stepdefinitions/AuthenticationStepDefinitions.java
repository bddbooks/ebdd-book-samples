package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.services.AuthenticationService;
import io.cucumber.java.en.*;

public class AuthenticationStepDefinitions {
    @Given("the customer {string} is authenticated")
    public void theCustomerIsAuthenticated(String customerName) {
        AuthenticationService.login(customerName);
    }
}
