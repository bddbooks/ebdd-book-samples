package com.wimp.app.specs.support;

import com.wimp.app.models.OrderStatus;

public class NamedOrderData {
    private String orderName = "";
    private OrderStatus status = OrderStatus.PLACED;

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
