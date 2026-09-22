package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.specs.drivers.NotificationsApiDriver;
import com.wimp.app.specs.support.AuthenticationContext;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotificationsStepDefinitions {
    private final AuthenticationContext authContext;
    private final NotificationsApiDriver notificationsApiDriver;

    public NotificationsStepDefinitions(AuthenticationContext authenticationContext, NotificationsApiDriver notificationsApiDriver) {
        this.authContext = authenticationContext;
        this.notificationsApiDriver = notificationsApiDriver;
    }

    @Then("they should receive a notification about the cancellation")
    public void theyShouldReceiveANotificationAboutTheCancellation() throws Exception {
        var customerName = authContext.getAuthenticatedCustomerNameVerified();
        var notifications = notificationsApiDriver.getNotifications(customerName);

        assertNotNull(notifications);
        assertTrue(notifications.stream().anyMatch(n -> n.message().contains("cancelled")),
            "Expected a cancellation notification but none was found.");
    }
}
