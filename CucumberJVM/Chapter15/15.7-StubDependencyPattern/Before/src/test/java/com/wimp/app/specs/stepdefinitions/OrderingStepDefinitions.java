package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.restapi.PlaceOrderRequest;
import com.wimp.app.specs.drivers.OrderingApiDriver;
import com.wimp.app.specs.support.OrderData;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.java.en.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class OrderingStepDefinitions {
    private final OrderingApiDriver orderingApiDriver;

    public OrderingStepDefinitions(OrderingApiDriver orderingApiDriver) {
        this.orderingApiDriver = orderingApiDriver;
    }

    @Given("they have placed an order with")
    public void theyHavePlacedAnOrderWith(List<OrderData> orderDataList) throws Exception {
        var orderData = orderDataList.getFirst(); // a single-row data table is expected for this step
        var expectedDeliveryTime = LocalDateTime.now().with(orderData.getExpectedDeliveryTime());
        //NOTE: The expectedDeliveryTime is not used, because of the workaround we apply. It will be used once the pattern is applied.

        // With the real time service we cannot fast-forward time, so cannot use the specified
        // expectedDeliveryTime. Instead, we force the expected delivery time being in 0.5 seconds,
        // and we wait in the whenTheDeliveryHasNotBeenMadeBy method for the background timer loop
        // to process the subscription.
        PlaceOrderRequest placeOrderRequest = new PlaceOrderRequestObjectMother()
            .withExpectedDeliveryTime(LocalDateTime.now().plusNanos(500_000_000))
            .build();

        orderingApiDriver.placeOrder(placeOrderRequest).execute();
    }

    @When("the delivery has not been made by {time}")
    public void whenTheDeliveryHasNotBeenMadeBy(LocalTime time) throws InterruptedException {
        //WORKAROUND: see notes above!
        //NOTE: The time is not used, because of the workaround we apply. It will be used once the pattern is applied.
        Thread.sleep(2_000);
    }
}
