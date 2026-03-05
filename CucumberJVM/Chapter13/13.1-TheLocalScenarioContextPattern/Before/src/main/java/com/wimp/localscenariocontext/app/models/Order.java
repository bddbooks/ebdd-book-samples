package com.wimp.localscenariocontext.app.models;

public class Order {
    private final int orderNo;
    private final String customerName;
    private final String pizzaName;
    private OrderStatus status = OrderStatus.NEW;

    public Order(int orderNo, String customerName, String pizzaName) {
        this.orderNo = orderNo;
        this.customerName = customerName;
        this.pizzaName = pizzaName;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPizzaName() {
        return pizzaName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
