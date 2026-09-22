package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.Order;
import com.wimp.app.specs.drivers.AuthenticationApiDriver;
import com.wimp.app.specs.drivers.BackdoorApiDriver;
import com.wimp.app.specs.drivers.KitchenApiDriver;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.OrderData;
import com.wimp.app.specs.support.OrderingContext;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderProcessingStepDefinitions {
    private final OrderingContext orderingContext;
    private final AuthenticationApiDriver authenticationApiDriver;
    private final KitchenApiDriver kitchenApiDriver;
    private final BackdoorApiDriver backdoorApiDriver;

    public OrderProcessingStepDefinitions(OrderingContext orderingContext, AuthenticationApiDriver authenticationApiDriver,
                                          KitchenApiDriver kitchenApiDriver, BackdoorApiDriver backdoorApiDriver) {
        this.orderingContext = orderingContext;
        this.authenticationApiDriver = authenticationApiDriver;
        this.kitchenApiDriver = kitchenApiDriver;
        this.backdoorApiDriver = backdoorApiDriver;
    }

    @Given("the following orders have been placed")
    public void theFollowingOrdersHaveBeenPlaced(List<OrderData> orders) throws Exception {
        for (OrderData orderData : orders) {
            Order placedOrder = backdoorApiDriver
                .prepareOrder(DomainDefaults.CUSTOMER_NAME,
                    new PlaceOrderRequestObjectMother().withPlacingTime(orderData.getPlacedAt()).build(),
                    orderData.getStatus()).execute();
            orderingContext.getPlacedOrders().add(placedOrder);
        }
    }

    @When("a kitchen staff member asks for an order to work on")
    public void aKitchenStaffMemberAsksForAnOrderToWorkOn() throws Exception {
        authenticationApiDriver.login(DomainDefaults.KITCHEN_STAFF, DomainDefaults.PASSWORD).execute();
        orderingContext.setTakenOrderNo(kitchenApiDriver.takeNextOrder().execute().getOrderNo());
    }

    @Then("{order} should be taken")
    public void theOrderShouldBeTaken(int expectedOrderNo) {
        assertNotNull(orderingContext.getTakenOrderNo());
        Order expectedOrder = orderingContext.getPlacedOrders().stream()
            .filter(order -> order.getOrderNo() == expectedOrderNo).findFirst().orElseThrow();
        Order taken = orderingContext.getPlacedOrders().stream()
            .filter(order -> order.getOrderNo() == orderingContext.getTakenOrderNo()).findFirst().orElseThrow();
        assertEquals(expectedOrder.getOrderNo(), taken.getOrderNo());
    }
}
