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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderNo;
    private String customerName;
    private String customerEmail;
    private List<PizzaItem> items = new ArrayList<>();
    private String deliveryAddress;
    private OrderStatus status;
    private String statusMessage;
    private LocalTime placingTime;
    private LocalDateTime expectedDeliveryTime;
    private BigDecimal price = BigDecimal.ZERO;
    private DeliveryMethod deliveryMethod = DeliveryMethod.DELIVERY_TO_ADDRESS;
    private OrderCollectionDetails orderCollectionDetails;
    private ContactDetails contactDetails;

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

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public LocalDateTime getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(LocalDateTime expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public LocalTime getPlacingTime() {
        return placingTime;
    }

    public void setPlacingTime(LocalTime placingTime) {
        this.placingTime = placingTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public OrderCollectionDetails getOrderCollectionDetails() {
        return orderCollectionDetails;
    }

    public void setOrderCollectionDetails(OrderCollectionDetails orderCollectionDetails) {
        this.orderCollectionDetails = orderCollectionDetails;
    }

    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }

    @Override
    public String toString() {
        return "Order[orderNo=%d, customerName=%s, status=%s, items=%d, deliveryAddress=%s, expectedDeliveryTime=%s, price=%s]"
            .formatted(orderNo, customerName, status, items.size(), deliveryAddress, expectedDeliveryTime, price);
    }
}
