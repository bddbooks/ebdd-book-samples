package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.Notification;
import com.wimp.app.specs.drivers.NotificationsApiDriver;
import com.wimp.app.specs.support.DomainDefaults;
import io.cucumber.java.en.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotificationsStepDefinitions {
    private final NotificationsApiDriver notificationsApiDriver;

    public NotificationsStepDefinitions(NotificationsApiDriver notificationsApiDriver) {
        this.notificationsApiDriver = notificationsApiDriver;
    }

    @Then("the customer should receive a notification about the delay")
    public void theCustomerShouldReceiveANotificationAboutTheDelay() throws Exception {
        List<Notification> notifications = notificationsApiDriver.getNotifications(DomainDefaults.CUSTOMER_NAME).execute();

        assertTrue(
            notifications.stream().anyMatch(n -> n.message().toLowerCase().contains("delayed")),
            "Expected a delay notification but none was found.");
    }
}
