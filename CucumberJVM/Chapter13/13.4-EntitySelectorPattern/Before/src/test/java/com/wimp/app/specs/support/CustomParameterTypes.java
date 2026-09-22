package com.wimp.app.specs.support;

import com.wimp.app.models.*;
import io.cucumber.java.ParameterType;

import java.util.Optional;

public class CustomParameterTypes {

    private final OrderingContext orderingContext;

    public CustomParameterTypes(OrderingContext orderingContext) {
        this.orderingContext = orderingContext;
    }

    @ParameterType("the order|the placed order")
    public int order(String value) {
        if (value.matches("the order|the placed order")){
            Optional.ofNullable(orderingContext.getCurrentOrderNo())
                .orElseThrow(() -> new RuntimeException("No current order"));
        }
        throw new RuntimeException("Invalid order value: " + value);
    }
}
