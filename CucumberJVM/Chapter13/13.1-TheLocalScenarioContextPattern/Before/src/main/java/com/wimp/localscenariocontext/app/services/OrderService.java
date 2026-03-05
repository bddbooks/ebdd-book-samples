package com.wimp.localscenariocontext.app.services;

import com.wimp.localscenariocontext.app.infrastructure.DataContext;
import com.wimp.localscenariocontext.app.models.Order;
import com.wimp.localscenariocontext.app.models.OrderStatus;

public class OrderService {
    public static void placeOrder(String customerName, int orderNo, String pizzaName) {
        if (!AuthenticationService.isLoggedIn(customerName)) {
            throw new RuntimeException("Customer " + customerName + " is not logged in.");
        }

        Order order = new Order(orderNo, customerName, pizzaName);
        setStatus(order, OrderStatus.PLACED);
        DataContext.INSTANCE.saveOrder(order);
    }

    private static void setStatus(Order order, OrderStatus status) {
        order.setStatus(status);
    }

    public static void cancelOrder(String customerName, int orderNo) {
        Order order = DataContext.INSTANCE.getOrderByOrderNr(orderNo);
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
