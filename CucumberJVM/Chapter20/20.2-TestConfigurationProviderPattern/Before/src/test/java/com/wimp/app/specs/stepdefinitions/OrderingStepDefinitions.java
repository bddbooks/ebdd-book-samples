package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.specs.drivers.AuthenticationApiDriver;
import com.wimp.app.specs.drivers.OrderingApiDriver;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.OrderingContext;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.java.en.*;

public class OrderingStepDefinitions {
    private final OrderingContext orderingContext;
    private final AuthenticationApiDriver authenticationApiDriver;
    private final OrderingApiDriver orderingApiDriver;

    public OrderingStepDefinitions(OrderingContext orderingContext, AuthenticationApiDriver authenticationApiDriver, OrderingApiDriver orderingApiDriver) {
        this.orderingContext = orderingContext;
        this.authenticationApiDriver = authenticationApiDriver;
        this.orderingApiDriver = orderingApiDriver;
    }

    @Given("an authenticated customer has placed an order")
    public void anAuthenticatedCustomerHasPlacedAnOrder() throws Exception {
        authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.PASSWORD).execute();
        var placeOrderRequest = new PlaceOrderRequestObjectMother().build();
        var placedOrder = orderingApiDriver.placeOrder(placeOrderRequest).execute();
        orderingContext.setCurrentOrderNo(placedOrder.getOrderNo());
    }
}
