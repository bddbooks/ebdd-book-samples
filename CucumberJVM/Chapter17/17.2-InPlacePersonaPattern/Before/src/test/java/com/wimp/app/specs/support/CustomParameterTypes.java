package com.wimp.app.specs.support;

import com.wimp.app.models.OrderStatus;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public class CustomParameterTypes {

    private final OrderingContext orderingContext;

    public CustomParameterTypes(OrderingContext orderingContext) {
        this.orderingContext = orderingContext;
    }

    @ParameterType("the order|the placed order|order \\d+")
    public int order(String value) {
        if (value.matches("the order|the placed order")){
            return orderingContext.getCurrentOrderNoVerified();
        }
        if (value.matches("order \\d+")){
            return Integer.parseInt(value.substring("order ".length()));
        }
        throw new RuntimeException("Invalid order value: " + value);
    }

    @DataTableType
    public OrderByNumberData orderByNumberDataRow(Map<String, String> row) {
        var orderData = new OrderByNumberData();
        if (row.containsKey("order no"))
            orderData.setOrderNo(Integer.parseInt(row.get("order no")));
        if (row.containsKey("status"))
            orderData.setStatus(OrderStatus.valueOf(row.get("status").toUpperCase()));
        return orderData;
    }

    @ParameterType("\\d{4}-\\d{2}-\\d{2}")
    public LocalDate date(String value) {
        return LocalDate.parse(value);
    }

    @ParameterType("\\$?(\\d+(?:\\.\\d{1,2})?)")
    public BigDecimal price(String value){
        return new BigDecimal(value);
    }
}
