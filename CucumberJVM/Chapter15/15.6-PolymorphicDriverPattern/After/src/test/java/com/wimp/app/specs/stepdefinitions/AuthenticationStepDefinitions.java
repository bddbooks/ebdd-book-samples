package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.restapi.LoginResponse;
import com.wimp.app.specs.drivers.AuthenticationDriver;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.TestActionResult;
import io.cucumber.java.en.*;

public class AuthenticationStepDefinitions {
    private final AuthenticationDriver authenticationDriver;
    private TestActionResult<LoginResponse> loginResult;

    public AuthenticationStepDefinitions(AuthenticationDriver authenticationDriver) {
        this.authenticationDriver = authenticationDriver;
    }

    @When("the customer attempts to log in with a wrong password")
    public void theCustomerAttemptsToLogInWithAWrongPassword() throws Exception {
        loginResult = authenticationDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.WRONG_PASSWORD).attemptExecute();
    }

    @Then("the login should fail with {string}")
    public void theLoginShouldFailWith(String expectedMessage) {
        loginResult.assertFailedWithErrorMessageContains(expectedMessage);
    }
}
