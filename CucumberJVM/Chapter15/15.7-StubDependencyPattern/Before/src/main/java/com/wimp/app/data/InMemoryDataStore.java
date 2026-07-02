package com.wimp.app.data;

import com.wimp.app.models.Notification;
import com.wimp.app.models.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryDataStore {
    private final AtomicInteger nextOrderNo = new AtomicInteger(1);
    private final Map<Integer, Order> ordersByOrderNo = new ConcurrentHashMap<>();
    private final Map<String, List<Notification>> notificationsByCustomer = new ConcurrentHashMap<>();
    private final Map<String, String> passwordsByCustomer = new ConcurrentHashMap<>();

    public void reset() {
        nextOrderNo.set(1);
        ordersByOrderNo.clear();
        notificationsByCustomer.clear();
        passwordsByCustomer.clear();
        passwordsByCustomer.put("Rebecca", "Pa22w0rd!");
    }

    public boolean validatePassword(String customerName, String password) {
        return password.equals(passwordsByCustomer.get(customerName));
    }

    public Order insertOrder(Order order) {
        int orderNo = nextOrderNo.getAndIncrement();
        order.setOrderNo(orderNo);
        ordersByOrderNo.put(orderNo, order);
        return order;
    }

    public Order getOrderByOrderNo(int orderNo) {
        return ordersByOrderNo.get(orderNo);
    }

    public void saveNotification(String customerName, String message) {
        notificationsByCustomer.computeIfAbsent(customerName, _ -> new ArrayList<>())
            .add(new Notification(customerName, message));
    }

    public List<Notification> getNotifications(String customerName) {
        return new ArrayList<>(notificationsByCustomer.getOrDefault(customerName, List.of()));
    }
}
