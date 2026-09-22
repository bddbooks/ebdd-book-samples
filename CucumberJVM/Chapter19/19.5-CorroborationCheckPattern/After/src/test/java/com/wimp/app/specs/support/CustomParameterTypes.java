package com.wimp.app.specs.support;

import com.wimp.app.models.*;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.Map;

public class CustomParameterTypes {

    private final OrderingContext orderingContext;

    public CustomParameterTypes(OrderingContext orderingContext) {
        this.orderingContext = orderingContext;
    }

    @ParameterType("the order|the placed order|the earliest order received|order [A-Z]")
    public int order(String value) {
        if (value.matches("the order|the placed order")){
            return orderingContext.getCurrentOrderNoVerified();
        }
        if (value.matches("the earliest order received")){
            return orderingContext.getPlacedOrders().stream()
                .min(Comparator.comparing(Order::getPlacingTime))
                .map(Order::getOrderNo)
                .orElseThrow(() -> new RuntimeException("No orders available"));
        }
        if (value.matches("order [A-Z]")){
            String name = value.substring(value.length() - 1);
            Integer orderNo = orderingContext.getNamedOrders().get(name);
            if (orderNo == null) {
                throw new RuntimeException("Order " + name + " not known");
            }
            return orderNo;
        }
        throw new RuntimeException("Invalid order value: " + value);
    }

    @DataTableType
    public OrderData orderDataRow(Map<String, String> row) {
        var orderData = new OrderData();
        if (row.containsKey("order name"))
            orderData.setOrderName(row.get("order name"));
        if (row.containsKey("placed at"))
            orderData.setPlacedAt(LocalTime.parse(row.get("placed at")));
        if (row.containsKey("expected delivery time"))
            orderData.setExpectedDeliveryTime(LocalTime.parse(row.get("expected delivery time")));
        if (row.containsKey("status"))
            orderData.setStatus(OrderStatus.valueOf(row.get("status").toUpperCase()));
        return orderData;
    }
}
