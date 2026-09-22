package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.specs.drivers.OrderingApiDriver;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.java.en.*;

import java.util.Optional;

public class OrderingStepDefinitions {
    private final OrderingApiDriver orderingApiDriver;

    private Integer placedOrderNo;

    public OrderingStepDefinitions(OrderingApiDriver orderingApiDriver) {
        this.orderingApiDriver = orderingApiDriver;
    }

    @Given("they have placed an order")
    public void theyHavePlacedAnOrder() throws Exception {
        var placeOrderRequest = new PlaceOrderRequestObjectMother().build();
        var placedOrder = orderingApiDriver.placeOrder(placeOrderRequest);
        placedOrderNo = placedOrder.getOrderNo();
    }

    @When("they cancel the placed order")
    public void theyCancelTheOrder() throws Exception {
        int orderNo = Optional.ofNullable(placedOrderNo).orElseThrow(() -> new RuntimeException("No placed order"));
        orderingApiDriver.cancelOrder(orderNo);
    }
}
