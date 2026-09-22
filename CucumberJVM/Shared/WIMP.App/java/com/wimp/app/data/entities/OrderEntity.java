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

package com.wimp.app.data.entities;

import com.wimp.app.models.DeliveryMethod;
import com.wimp.app.models.OrderStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderNo;

    @Column(nullable = false, length = 200)
    private String customerName;

    @Column(nullable = false, length = 320)
    private String customerEmail;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_no"))
    @OrderColumn(name = "item_index")
    private List<PizzaItemEmbeddable> items = new ArrayList<>();

    @Column(nullable = false, length = 500)
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderStatus status;

    @Column(length = 500)
    private String statusMessage;

    @Column(nullable = false)
    private LocalTime placingTime;

    @Column(nullable = false)
    private LocalDateTime expectedDeliveryTime;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DeliveryMethod deliveryMethod = DeliveryMethod.DELIVERY_TO_ADDRESS;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "orderNo", column = @Column(name = "collection_order_no")),
        @AttributeOverride(name = "boxesToBeCollected", column = @Column(name = "collection_boxes_to_be_collected")),
        @AttributeOverride(name = "contactDetailsConfirmationRequested", column = @Column(name = "collection_contact_details_confirmation_requested"))
    })
    private OrderCollectionDetailsEmbeddable orderCollectionDetails;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "contact_name")),
        @AttributeOverride(name = "email", column = @Column(name = "contact_email")),
        @AttributeOverride(name = "phone", column = @Column(name = "contact_phone")),
        @AttributeOverride(name = "country", column = @Column(name = "contact_country")),
        @AttributeOverride(name = "state", column = @Column(name = "contact_state"))
    })
    private ContactDetailsEmbeddable contactDetails;

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
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

    public List<PizzaItemEmbeddable> getItems() {
        return items;
    }

    public void setItems(List<PizzaItemEmbeddable> items) {
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
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

    public LocalDateTime getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(LocalDateTime expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
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

    public OrderCollectionDetailsEmbeddable getOrderCollectionDetails() {
        return orderCollectionDetails;
    }

    public void setOrderCollectionDetails(OrderCollectionDetailsEmbeddable orderCollectionDetails) {
        this.orderCollectionDetails = orderCollectionDetails;
    }

    public ContactDetailsEmbeddable getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ContactDetailsEmbeddable contactDetails) {
        this.contactDetails = contactDetails;
    }
}
