package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.specs.drivers.AuthenticationApiDriver;
import com.wimp.app.specs.support.AuthenticationContext;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.TestActionFailedException;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationStepDefinitions {
    private final AuthenticationContext authenticationContext;
    private final AuthenticationApiDriver authenticationApiDriver;
    private TestActionFailedException loginError;

    public AuthenticationStepDefinitions(AuthenticationContext authenticationContext, AuthenticationApiDriver authenticationApiDriver) {
        this.authenticationContext = authenticationContext;
        this.authenticationApiDriver = authenticationApiDriver;
    }

    @Given("the customer is authenticated")
    public void theCustomerIsAuthenticated() throws Exception {
        authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.PASSWORD).execute();
        authenticationContext.setAuthenticatedCustomerName(DomainDefaults.CUSTOMER_NAME);
    }

    @When("the customer attempts to log in with a wrong password")
    public void theCustomerAttemptsToLogInWithAWrongPassword() throws Exception {
        try {
            authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.WRONG_PASSWORD).execute();
            loginError = null;
        } catch (TestActionFailedException ex) {
            loginError = ex;
        }
    }

    @Then("the login should fail with {string}")
    public void theLoginShouldFailWith(String expectedMessage) {
        assertNotNull(loginError);
        assertTrue(loginError.getMessage().contains(expectedMessage), "Login should fail with the right error message (`%s`), but failed with `%s`".formatted(expectedMessage, loginError.getMessage()));
    }
}
