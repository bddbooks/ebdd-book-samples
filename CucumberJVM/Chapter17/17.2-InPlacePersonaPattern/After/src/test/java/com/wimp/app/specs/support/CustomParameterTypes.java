package com.wimp.app.specs.support;

import com.wimp.app.models.OrderStatus;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class CustomParameterTypes {

    private final OrderingContext orderingContext;

    public CustomParameterTypes(OrderingContext orderingContext) {
        this.orderingContext = orderingContext;
    }

    @ParameterType("the order|the placed order|order [A-Z]")
    public int order(String value) {
        if (value.matches("the order|the placed order")){
            return orderingContext.getCurrentOrderNoVerified();
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
    public NamedOrderData namedOrderDataRow(Map<String, String> row) {
        var orderData = new NamedOrderData();
        if (row.containsKey("order name"))
            orderData.setOrderName(row.get("order name"));
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
