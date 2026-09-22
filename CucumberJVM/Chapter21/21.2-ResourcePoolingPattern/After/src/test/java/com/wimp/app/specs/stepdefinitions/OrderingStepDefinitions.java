package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.specs.drivers.OrderingApiDriver;
import com.wimp.app.specs.support.AuthenticationContext;
import com.wimp.app.specs.support.OrderingContext;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.java.en.*;

public class OrderingStepDefinitions {
    private final OrderingContext orderingContext;
    private final AuthenticationContext authenticationContext;
    private final OrderingApiDriver orderingApiDriver;

    public OrderingStepDefinitions(OrderingContext orderingContext, AuthenticationContext authenticationContext, OrderingApiDriver orderingApiDriver) {
        this.orderingContext = orderingContext;
        this.authenticationContext = authenticationContext;
        this.orderingApiDriver = orderingApiDriver;
    }

    @Given("the customer has placed an order containing a {string} pizza")
    public void theCustomerHasPlacedAnOrderContainingAPizza(String pizzaName) throws Exception {
        authenticationContext.ensureAuthenticatedCustomer();
        var placeOrderRequest = new PlaceOrderRequestObjectMother().withItem(pizzaName).build();
        var placedOrder = orderingApiDriver.placeOrder(placeOrderRequest).execute();
        orderingContext.setCurrentOrderNo(placedOrder.getOrderNo());
    }

    @When("the order is delivered")
    public void theOrderIsDelivered() throws Exception {
        orderingApiDriver.deliverOrder(orderingContext.getCurrentOrderNoVerified()).execute();
    }
}
