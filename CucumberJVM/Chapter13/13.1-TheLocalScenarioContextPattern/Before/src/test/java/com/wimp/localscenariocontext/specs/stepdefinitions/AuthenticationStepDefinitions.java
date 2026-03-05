package com.wimp.localscenariocontext.specs.stepdefinitions;

import com.wimp.localscenariocontext.app.services.AuthenticationService;
import io.cucumber.java.en.Given;

public class AuthenticationStepDefinitions {
    @Given("the customer {word} has logged in")
    public void theCustomerHasLoggedIn(String customerName) {
        AuthenticationService.login(customerName);
    }
}
