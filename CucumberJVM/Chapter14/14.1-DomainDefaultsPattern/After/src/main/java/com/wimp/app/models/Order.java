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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderNo;
    private String customerName;
    private String customerEmail;
    private List<PizzaItem> items = new ArrayList<>();
    private OrderStatus status;
    private LocalDateTime placingTime;

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<PizzaItem> getItems() {
        return items;
    }

    public void setItems(List<PizzaItem> items) {
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
    }


    public void addItem(String pizzaName, String size) {
        items.add(new PizzaItem(pizzaName, size));
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getPlacingTime() {
        return placingTime;
    }

    public void setPlacingTime(LocalDateTime placingTime) {
        this.placingTime = placingTime;
    }

    @Override
    public String toString() {
        return "Order[orderNo=%d, customerName=%s, status=%s, items=%d]"
            .formatted(orderNo, customerName, status, items.size());
    }
}
