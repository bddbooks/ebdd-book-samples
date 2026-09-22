package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.infrastructure.DataRepository;
import com.wimp.app.services.NotificationService;
import com.wimp.app.services.OrderService;
import com.wimp.app.specs.support.AuthenticationContext;
import com.wimp.app.specs.support.OrderingContext;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderingStepDefinitions {
    private final AuthenticationContext authenticationContext;
    private final OrderingContext orderingContext;

    public OrderingStepDefinitions(AuthenticationContext authenticationContext, OrderingContext orderingContext) {
        this.authenticationContext = authenticationContext;
        this.orderingContext = orderingContext;
    }

    @Given("the authenticated customer has placed an order")
    public void theAuthenticatedCustomerHasPlacedAnOrder() {
        var order = OrderService.placeOrder(authenticationContext.getAuthenticatedCustomerName(), "Margherita");
        orderingContext.setCurrentOrderNo(order.getOrderNo());
    }

    @When("the authenticated customer cancels {order}")
    public void theAuthenticatedCustomerCancelsTheOrder(int orderNo) {
        OrderService.cancelOrder(authenticationContext.getAuthenticatedCustomerName(), orderNo);
    }

    @Then("the authenticated customer should receive a notification about the cancellation")
    public void theAuthenticatedCustomerShouldReceiveANotificationAboutTheCancellation() {
        assertTrue(NotificationService.wasNotificationSent(authenticationContext.getAuthenticatedCustomerName()));
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
