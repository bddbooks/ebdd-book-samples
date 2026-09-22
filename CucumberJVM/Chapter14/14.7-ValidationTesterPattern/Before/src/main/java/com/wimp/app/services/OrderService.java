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

package com.wimp.app.services;

import com.wimp.app.infrastructure.DataRepository;
import com.wimp.app.models.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

@Component
public class OrderService {
    private final DataRepository dataRepository;
    private final PromotionService promotionService;
    private final EmailService emailService;

    public OrderService(DataRepository dataRepository, PromotionService promotionService, EmailService emailService) {
        this.dataRepository = dataRepository;
        this.promotionService = promotionService;
        this.emailService = emailService;
    }

    public Order placeOrder(Order order) {
        var customerName = Optional.ofNullable(AuthenticationService.getAuthenticatedCustomerName())
            .orElseThrow(() -> new RuntimeException("Customer is not authenticated."));

        order.setOrderNo(dataRepository.getNextOrderNo());
        order.setCustomerName(customerName);
        order.setPlacingTime(LocalDateTime.now());

        var hasTooManyLargePizzas = ((int) order.getItems().stream().filter(item -> item.getSize() == PizzaSize.LARGE).count()) > 4;
        setStatus(order, hasTooManyLargePizzas ? OrderStatus.REJECTED : OrderStatus.PLACED);
        dataRepository.saveOrder(order);
        return order;
    }

    private static void setStatus(Order order, OrderStatus status) {
        order.setStatus(status);
    }

    public Order getOrder(int orderNo) {
        return dataRepository.getOrderByOrderNr(orderNo);
    }

    public void provideContactDetails(Order order, ContactDetails contactDetails) {
        String validationErrorMessage = validateContactDetails(contactDetails);
        if (validationErrorMessage == null && order.getDeliveryMethod() == DeliveryMethod.CUSTOMER_COLLECTION) {
            validationErrorMessage = validateCustomerCollectionContactDetails(contactDetails);
        }

        if (validationErrorMessage != null) {
            throw new RuntimeException(validationErrorMessage);
        }

        order.setContactDetails(contactDetails);
    }

    private String validateCustomerCollectionContactDetails(ContactDetails contactDetails) {
        if ((contactDetails.getEmail() == null || contactDetails.getEmail().isBlank())
            && (contactDetails.getPhone() == null || contactDetails.getPhone().isBlank())) {
            return "For customer collection email or phone must be specified";
        }
        return null;
    }

    private String validateContactDetails(ContactDetails contactDetails) {
        if (contactDetails.getName() == null || contactDetails.getName().isBlank()) {
            return "Name not specified";
        }
        if (contactDetails.getEmail() != null && !contactDetails.getEmail().isBlank() && !isValidEmail(contactDetails.getEmail())) {
            return "Wrong email format";
        }
        if (contactDetails.getPhone() != null && !contactDetails.getPhone().isBlank() && !isValidPhone(contactDetails.getPhone())) {
            return "Wrong phone number format";
        }
        if ("US".equals(contactDetails.getCountry()) && (contactDetails.getState() == null || contactDetails.getState().isBlank() || "-".equals(contactDetails.getState()))) {
            return "For US country the state must be specified";
        }
        return null;
    }

    private boolean isValidPhone(String phone) {
        return phone.length() >= 6 && phone.chars().allMatch(Character::isDigit);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.indexOf('@') > 0 && email.lastIndexOf('.') > email.indexOf('@');
    }

    public Order startWorkOnNextOrder()
    {
        var nextOrder = dataRepository.getPlacedOrders()
            .stream().min(Comparator.comparing(Order::getPlacingTime));

        if (nextOrder.isEmpty())
        {
            return null;
        }

        setStatus(nextOrder.get(), OrderStatus.IN_PREPARATION);
        return nextOrder.get();
    }

    public void deliverOrder(int orderNo) {
        var order = Optional.ofNullable(dataRepository.getOrderByOrderNr(orderNo))
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.REJECTED)
        {
            throw new RuntimeException("Order is rejected");
        }

        setStatus(order, OrderStatus.COMPLETED);

        // Check if order contains Margherita pizza and Margherita Friday promotion is active
        if (order.getItems().stream().anyMatch(i -> i.getName().equals("Margherita")) &&
            promotionService.isPromotionActive("Margherita Friday"))
        {
            emailService.sendCouponEmail(order.getCustomerName(), "MARGHERITA25");
        }
    }

    public void setWaitingForPickup(int orderNo)
    {
        var order = Optional.of(dataRepository.getOrderByOrderNr(orderNo))
            .orElseThrow(() -> new RuntimeException("Order not found"));
        setStatus(order, OrderStatus.WAITING_FOR_PICKUP);
    }

    public void changeDeliveryAddress(int orderNo, String newAddress)
    {
        var order = Optional.of(dataRepository.getOrderByOrderNr(orderNo))
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!canChangeAddress(order))
        {
            throw new RuntimeException("Cannot change delivery address after pickup.");
        }

        order.setDeliveryAddress(newAddress);
    }

    private boolean canChangeAddress(Order order)
    {
        // Simulated logic: address can be changed if order hasn't been picked up
        return order.getStatus().compareTo(OrderStatus.WAITING_FOR_PICKUP) <= 0;
    }
}
