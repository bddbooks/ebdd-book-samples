package com.wimp.app.specs.support;

import com.wimp.app.models.OrderStatus;

public class OrderByNumberData {
    private Integer orderNo;
    private OrderStatus status = OrderStatus.PLACED;

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
