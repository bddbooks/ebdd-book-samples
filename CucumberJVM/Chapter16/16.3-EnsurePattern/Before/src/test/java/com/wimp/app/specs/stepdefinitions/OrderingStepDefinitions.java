package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.specs.drivers.OrderingApiDriver;
import com.wimp.app.specs.support.OrderingContext;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.java.en.*;

public class OrderingStepDefinitions {
    private final OrderingContext orderingContext;
    private final OrderingApiDriver orderingApiDriver;

    public OrderingStepDefinitions(OrderingContext orderingContext, OrderingApiDriver orderingApiDriver) {
        this.orderingContext = orderingContext;
        this.orderingApiDriver = orderingApiDriver;
    }

    @Given("they have placed an order for {int} pizzas")
    public void theyHavePlacedAnOrderForPizzas(int count) throws Exception {
        var placeOrderRequest = new PlaceOrderRequestObjectMother().withItems(count).build();
        var placedOrder = orderingApiDriver.placeOrder(placeOrderRequest).execute();
        orderingContext.setCurrentOrderNo(placedOrder.getOrderNo());
    }
}
