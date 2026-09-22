package com.wimp.app.specs.support;

import com.wimp.app.models.*;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;

import java.time.LocalTime;
import java.util.Map;

public class CustomParameterTypes {

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

    @ParameterType("\\d{2}:\\d{2}")
    public LocalTime time(String value) {
        return LocalTime.parse(value);
    }
}
