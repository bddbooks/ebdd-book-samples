package com.wimp.app.specs.support;

import com.wimp.app.models.*;
import io.cucumber.java.ParameterType;

import java.util.Comparator;
import java.util.Optional;

public class CustomParameterTypes {

    private final OrderingContext orderingContext;

    public CustomParameterTypes(OrderingContext orderingContext) {
        this.orderingContext = orderingContext;
    }

    @ParameterType("the order|the placed order|the earliest order received")
    public int order(String value) {
        if (value.matches("the order|the placed order")){
            Optional.ofNullable(orderingContext.getCurrentOrderNo())
                .orElseThrow(() -> new RuntimeException("No current order"));
        }
        if (value.matches("the earliest order received")){
            return orderingContext.getPlacedOrders().stream()
                .min(Comparator.comparing(Order::getPlacingTime))
                .map(Order::getOrderNo)
                .orElseThrow(() -> new RuntimeException("No orders available"));
        }
        throw new RuntimeException("Invalid order value: " + value);
    }
}
