package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.OrderStatus;
import com.wimp.app.specs.drivers.*;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.OrderingContext;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.java.en.*;

public class OrderingStepDefinitions {
    private final OrderingContext orderingContext;
    private final BackdoorApiDriver backdoorApiDriver;

    public OrderingStepDefinitions(OrderingContext orderingContext, BackdoorApiDriver backdoorApiDriver) {
        this.orderingContext = orderingContext;
        this.backdoorApiDriver = backdoorApiDriver;
    }

    @Given("the customer has an order that is waiting for pickup")
    public void theCustomerHasAnOrderThatIsWaitingForPickup() throws Exception {
        var orderRequest = new PlaceOrderRequestObjectMother().build();
        var placedOrder = backdoorApiDriver
            .prepareOrder(DomainDefaults.CUSTOMER_NAME, orderRequest, OrderStatus.WAITING_FOR_PICKUP)
            .execute();
        orderingContext.setCurrentOrderNo(placedOrder.getOrderNo());
    }
}
