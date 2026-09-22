package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.Order;
import com.wimp.app.specs.drivers.*;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.OrderingContext;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.java.en.*;

public class OrderingStepDefinitions {
    private final OrderingContext orderingContext;
    private final AuthenticationApiDriver authenticationApiDriver;
    private final OrderingApiDriver orderingApiDriver;
    private final KitchenApiDriver kitchenApiDriver;

    public OrderingStepDefinitions(OrderingContext orderingContext, AuthenticationApiDriver authenticationApiDriver, OrderingApiDriver orderingApiDriver, KitchenApiDriver kitchenApiDriver) {
        this.orderingContext = orderingContext;
        this.authenticationApiDriver = authenticationApiDriver;
        this.orderingApiDriver = orderingApiDriver;
        this.kitchenApiDriver = kitchenApiDriver;
    }

    @Given("the customer has an order that is waiting for pickup")
    public void theCustomerHasAnOrderThatIsWaitingForPickup() throws Exception {
        authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.PASSWORD)
            .execute();
        var orderRequest = new PlaceOrderRequestObjectMother().build();
        var placedOrder = orderingApiDriver.placeOrder(orderRequest).execute();
         authenticationApiDriver.login(DomainDefaults.KITCHEN_STAFF, DomainDefaults.PASSWORD)
            .execute();

        Order takenOrder = null;
        while (takenOrder == null || takenOrder.getOrderNo() != placedOrder.getOrderNo())
        {
            takenOrder = kitchenApiDriver.takeNextOrder().execute();
        }

        kitchenApiDriver.setReady(placedOrder.getOrderNo()).execute();
        orderingContext.setCurrentOrderNo(placedOrder.getOrderNo());
    }
}
