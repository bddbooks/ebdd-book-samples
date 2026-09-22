package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.Notification;
import com.wimp.app.specs.support.AuthenticationContext;
import io.cucumber.java.en.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotificationsStepDefinitions {
    private final AuthenticationContext authContext;
    private final RestTestClient restTestClient;

    public NotificationsStepDefinitions(AuthenticationContext authenticationContext, WebApplicationContext context) {
        this.authContext = authenticationContext;
        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
    }

    @Then("they should receive a notification about the cancellation")
    public void theyShouldReceiveANotificationAboutTheCancellation() throws Exception {
        var customerName = authContext.getAuthenticatedCustomerNameVerified();
        var response = restTestClient.get().uri("/api/notifications/{customerName}", customerName)
            .accept(MediaType.APPLICATION_JSON)
            .exchange();
        response.expectStatus().is2xxSuccessful();
        var notifications = response.expectBody(new ParameterizedTypeReference<List<Notification>>() {}).returnResult().getResponseBody();
        assertNotNull(notifications);
        assertTrue(notifications.stream().anyMatch(n -> n.message().contains("cancelled")),
            "Expected a cancellation notification but none was found.");
    }
}
