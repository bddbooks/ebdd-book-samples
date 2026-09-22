package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.restapi.ErrorResponse;
import com.wimp.app.restapi.LoginRequest;
import com.wimp.app.restapi.LoginResponse;
import com.wimp.app.specs.support.AuthenticationContext;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.RestApiContext;
import io.cucumber.java.en.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationStepDefinitions {
    private final AuthenticationContext authenticationContext;
    private final RestApiContext restApiContext;
    private final RestTestClient restTestClient;
    private RestTestClient.ResponseSpec loginApiResponse;

    public AuthenticationStepDefinitions(AuthenticationContext authenticationContext, RestApiContext restApiContext, WebApplicationContext context) {
        this.authenticationContext = authenticationContext;
        this.restApiContext = restApiContext;
        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
    }

    @Given("the customer is authenticated")
    public void theCustomerIsAuthenticated() throws Exception {
        var response = restTestClient.post().uri("/api/auth/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new LoginRequest(DomainDefaults.CUSTOMER_NAME, DomainDefaults.PASSWORD))
            .exchange();
        response.expectStatus().is2xxSuccessful();
        var loginResponse =
            Optional.ofNullable(response.expectBody(LoginResponse.class).returnResult().getResponseBody())
                .orElseThrow(() -> new RuntimeException("No result payload found"));
        restApiContext.setBearerToken(loginResponse.token());
        authenticationContext.setAuthenticatedCustomerName(DomainDefaults.CUSTOMER_NAME);
    }

    @When("the customer attempts to log in with a wrong password")
    public void theCustomerAttemptsToLogInWithAWrongPassword() throws Exception {
        loginApiResponse = restTestClient.post().uri("/api/auth/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new LoginRequest(DomainDefaults.CUSTOMER_NAME, DomainDefaults.WRONG_PASSWORD))
            .exchange();
    }

    @Then("the login should fail with {string}")
    public void theLoginShouldFailWith(String expectedMessage) {
        assertNotNull(loginApiResponse);
        loginApiResponse.expectStatus().isUnauthorized();
        var errorMessage = loginApiResponse.expectBody(ErrorResponse.class).returnResult().getResponseBody().error();
        assertTrue(errorMessage.contains(expectedMessage), "Login should fail with the right error message (`%s`), but failed with `%s`".formatted(expectedMessage, errorMessage));
    }
}
