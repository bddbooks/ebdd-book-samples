package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.restapi.PlaceOrderRequest;
import com.wimp.app.specs.drivers.OrderingApiDriver;
import com.wimp.app.specs.drivers.TimeServiceDriver;
import com.wimp.app.specs.support.OrderData;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.java.en.*;

import java.time.LocalTime;
import java.util.List;

public class OrderingStepDefinitions {
    private final OrderingApiDriver orderingApiDriver;
    private final TimeServiceDriver timeServiceDriver;

    public OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, TimeServiceDriver timeServiceDriver) {
        this.orderingApiDriver = orderingApiDriver;
        this.timeServiceDriver = timeServiceDriver;
    }

    @Given("they have placed an order with")
    public void theyHavePlacedAnOrderWith(List<OrderData> orderDataList) throws Exception {
        var orderData = orderDataList.getFirst(); // a single-row data table is expected for this step

        // ensuring that the placing time is before the expected delivery time
        timeServiceDriver.setCurrentTime(orderData.getExpectedDeliveryTime().minusMinutes(5));

        // preparing a place order request with expected delivery time (this setting is only available for testing)
        PlaceOrderRequest placeOrderRequest = new PlaceOrderRequestObjectMother()
            .withExpectedDeliveryTime(timeServiceDriver.getTodayTime(orderData.getExpectedDeliveryTime()))
            .build();

        orderingApiDriver.placeOrder(placeOrderRequest).execute();
    }

    @When("the delivery has not been made by {time}")
    public void whenTheDeliveryHasNotBeenMadeBy(LocalTime time) {
        timeServiceDriver.setCurrentTime(time);
    }
}
