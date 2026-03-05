package com.wimp.localscenariocontext.specs.stepdefinitions;

import com.wimp.localscenariocontext.app.infrastructure.DataContext;
import com.wimp.localscenariocontext.app.services.NotificationService;
import com.wimp.localscenariocontext.app.services.OrderService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderingStepDefinitions {
    @Given("the customer {word} has placed the order #{int}")
    public void theCustomerHasPlacedTheOrder(String customerName, int orderNo) {
        OrderService.placeOrder(customerName, orderNo, "Margherita");
    }

    @When("the customer {word} cancels the order #{int}")
    public void theCustomerCancelsTheOrder(String customerName, int orderNo) {
        OrderService.cancelOrder(customerName, orderNo);
    }

    @Then("the customer {word} should receive a notification about the cancellation")
    public void theCustomerShouldReceiveANotification(String customerName) {
        assertTrue(NotificationService.wasNotificationSent(customerName));
    }

    /**
     * This hook resets the in-memory database before each scenario execution,
     * ensuring that each test starts with a clean state.
     * The pattern TODO1 contains a better approach for in-memory database management via dependencies.
     * The pattern TODO2 contains further options for dealing with shared resources, such as using a real database.
     */
    @Before
    public void resetDatabase() {
        DataContext.INSTANCE.reset();
    }
}
