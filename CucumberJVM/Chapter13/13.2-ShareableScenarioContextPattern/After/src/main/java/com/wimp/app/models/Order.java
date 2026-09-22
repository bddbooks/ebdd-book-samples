/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

package com.wimp.app.models;

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
