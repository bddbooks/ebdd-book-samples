package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.infrastructure.DataRepository;
import com.wimp.app.services.NotificationService;
import com.wimp.app.services.OrderService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderingStepDefinitions {
    @Given("the customer {string} has placed the order #{int}")
    public void theCustomerHasPlacedTheOrder(String customerName, int orderNo) {
        OrderService.placeOrder(customerName, orderNo, "Margherita");
    }

    @When("the customer {string} cancels the order #{int}")
    public void theCustomerCancelsTheOrder(String customerName, int orderNo) {
        OrderService.cancelOrder(customerName, orderNo);
    }

    @Then("the customer {string} should receive a notification about the cancellation")
    public void theCustomerShouldReceiveANotification(String customerName) {
        assertTrue(NotificationService.wasNotificationSent(customerName));
    }

    /**
     * This hook resets the in-memory database before each scenario execution,
     * ensuring that each test starts with a clean state.
     * The samples of chapter 14 demonstrate an improved approach by having a
     * stub implementation of the data repository and the samples of chapter 15
     * and later further improve that by using H2 in-memory database as stub at
     * Spring database layer. Finally, chapter 18 demonstrates how testing can
     * be done with real databases.
     */
    @Before
    public void resetDatabase() {
        DataRepository.INSTANCE.reset();
    }
}
