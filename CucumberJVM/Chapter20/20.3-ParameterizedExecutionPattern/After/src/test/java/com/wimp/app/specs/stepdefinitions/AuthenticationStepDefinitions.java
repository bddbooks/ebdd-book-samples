package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.restapi.LoginResponse;
import com.wimp.app.specs.drivers.AuthenticationApiDriver;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.TestActionResult;
import io.cucumber.java.en.*;

public class AuthenticationStepDefinitions {
    private final AuthenticationApiDriver authenticationApiDriver;
    private TestActionResult<LoginResponse> loginResult;

    public AuthenticationStepDefinitions(AuthenticationApiDriver authenticationApiDriver) {
        this.authenticationApiDriver = authenticationApiDriver;
    }

    @When("the customer attempts to log in with a wrong password")
    public void theCustomerAttemptsToLogInWithAWrongPassword() throws Exception {
        loginResult = authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.WRONG_PASSWORD).attemptExecute();
    }

    @Then("the login should fail with {string}")
    public void theLoginShouldFailWith(String expectedMessage) {
        loginResult.assertFailedWithErrorMessageContains(expectedMessage);
    }
}
