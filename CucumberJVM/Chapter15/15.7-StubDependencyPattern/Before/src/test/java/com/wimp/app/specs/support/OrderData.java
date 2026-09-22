package com.wimp.app.specs.support;

import com.wimp.app.models.OrderStatus;

import java.time.LocalTime;

public class OrderData {
    private String orderName = "";
    private LocalTime placedAt;
    private LocalTime expectedDeliveryTime;
    private OrderStatus status = OrderStatus.PLACED;

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public LocalTime getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalTime placedAt) {
        this.placedAt = placedAt;
    }

    public LocalTime getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(LocalTime expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
