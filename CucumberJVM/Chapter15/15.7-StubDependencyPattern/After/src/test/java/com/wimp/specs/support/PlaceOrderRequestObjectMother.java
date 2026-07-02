package com.wimp.specs.support;

import com.wimp.app.restapi.PlaceOrderRequest;

import java.time.LocalDateTime;

public class PlaceOrderRequestObjectMother {
    private LocalDateTime expectedDeliveryTime;

    public PlaceOrderRequest build() {
        return new PlaceOrderRequest(expectedDeliveryTime);
    }

    public PlaceOrderRequestObjectMother withExpectedDeliveryTime(LocalDateTime time) {
        this.expectedDeliveryTime = time;
        return this;
    }
}
