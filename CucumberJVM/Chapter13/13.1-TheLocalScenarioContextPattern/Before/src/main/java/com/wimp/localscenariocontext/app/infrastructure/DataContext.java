package com.wimp.localscenariocontext.app.infrastructure;

import com.wimp.localscenariocontext.app.models.Notification;
import com.wimp.localscenariocontext.app.models.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A simple simulation of a database context, using in-memory collections. In a real application,
 * this would likely be replaced with an actual database context or a repository pattern.
 */
public class DataContext {
    public static final DataContext INSTANCE = new DataContext();

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
