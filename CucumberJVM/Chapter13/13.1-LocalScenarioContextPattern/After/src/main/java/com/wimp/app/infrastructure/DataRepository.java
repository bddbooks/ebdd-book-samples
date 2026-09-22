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

package com.wimp.app.infrastructure;

import com.wimp.app.models.Notification;
import com.wimp.app.models.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A simple simulation of a database context, using in-memory collections. In a real application,
 * this would likely be replaced with an actual database context or a repository pattern.
 */
public class DataRepository {
    public static final DataRepository INSTANCE = new DataRepository();

    private final Map<Integer, Order> orders = new HashMap<>();
    private final List<Notification> notifications = new ArrayList<>();

    public void reset() {
        orders.clear();
        notifications.clear();
    }

    public List<Notification> getNotificationsByCustomerName(String customerName) {
        return notifications.stream()
                .filter(n -> n.getCustomerName().equals(customerName))
                .collect(Collectors.toList());
    }

    public void saveNotification(Notification notification) {
        notifications.add(notification);
    }

    public Order getOrderByOrderNr(int orderNr) {
        return orders.values().stream()
                .filter(o -> o.getOrderNo() == orderNr)
                .findFirst()
                .orElse(null);
    }

    public void saveOrder(Order order) {
        orders.put(order.getOrderNo(), order);
    }
}
