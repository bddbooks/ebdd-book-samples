package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.infrastructure.DataRepository;
import com.wimp.app.services.AuthenticationService;
import com.wimp.app.services.OrderService;
import com.wimp.app.specs.support.OrderingContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class OrderingStepDefinitions {
    private final OrderingContext orderingContext;

    public OrderingStepDefinitions(OrderingContext orderingContext) {
        this.orderingContext = orderingContext;
    }

    @Given("the following orders have been placed")
    public void theFollowingOrdersHaveBeenPlaced(DataTable ordersTable) {
        // In order to ensure the orders, we replay the ordering steps with a test customer.
        // A better approach to ensure this context is shown in Chapter 16, Context Shortcut pattern.
        AuthenticationService.login("Rebecca");
        for (var row : ordersTable.asMaps()) {
            // A better way of processing data tables is shown in Chapter 14, Data table accessor pattern.
            Integer orderNo = row.containsKey("order number") ? Integer.parseInt(row.get("order number")) : null;
            var placingTime = LocalTime.parse(row.get("placed at"));

            var order = OrderService.placeOrder("Rebecca", "Margherita", placingTime, orderNo);
            orderingContext.getPlacedOrders().add(order);
        }
    }

    @When("a kitchen staff member asks for an order to work on")
    public void aKitchenStaffMemberAsksForAnOrderToWorkOn() {
        var order = OrderService.startWorkOnNextOrder();
        if (order != null) {
            orderingContext.setTakenOrderNo(order.getOrderNo());
        }
    }

    @Then("the order placed at {word} should be taken")
    public void theOrderPlacedAtShouldBeTaken(String placedAt) {
        assertNotNull(orderingContext.getTakenOrderNo());
        var expectedTakenOrder = orderingContext.getPlacedOrders().stream()
            .filter(o -> o.getPlacingTime().equals(LocalTime.parse(placedAt)))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No expected order"));
        assertEquals(expectedTakenOrder.getOrderNo(), orderingContext.getTakenOrderNo());
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
