package com.wimp.app.specs.support;

import com.wimp.app.models.*;
import io.cucumber.java.ParameterType;

import java.util.Optional;

public class CustomParameterTypes {

    private final OrderingContext orderingContext;

    public CustomParameterTypes(OrderingContext orderingContext) {
        this.orderingContext = orderingContext;
    }

    @ParameterType("the order|the placed order|the new order")
    public int order(String value) {
        return Optional.ofNullable(orderingContext.getCurrentOrderNo())
            .orElseThrow(() -> new RuntimeException("No current order"));
    }
}
