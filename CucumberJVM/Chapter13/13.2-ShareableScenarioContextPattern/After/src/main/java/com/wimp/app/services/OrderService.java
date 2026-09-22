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
import com.wimp.app.models.Order;
import com.wimp.app.models.OrderStatus;

public class OrderService {
    public static void placeOrder(String customerName, int orderNo, String pizzaName) {
        if (!AuthenticationService.isAuthenticated(customerName)) {
            throw new RuntimeException("Customer " + customerName + " is not authenticated.");
        }

        Order order = new Order(orderNo, customerName, pizzaName);
        setStatus(order, OrderStatus.PLACED);
        DataRepository.INSTANCE.saveOrder(order);
    }

    private static void setStatus(Order order, OrderStatus status) {
        order.setStatus(status);
    }

    public static void cancelOrder(String customerName, int orderNo) {
        Order order = DataRepository.INSTANCE.getOrderByOrderNr(orderNo);
        if (order == null) {
            throw new RuntimeException("Order " + orderNo + " does not exist.");
        }

        if (!order.getCustomerName().equals(customerName)) {
            throw new RuntimeException("Order " + orderNo + " belongs to " + order.getCustomerName() + ", not " + customerName + ".");
        }

        NotificationService.sendCancellationNotification(customerName);
        order.setStatus(OrderStatus.CANCELLED);
    }
}
