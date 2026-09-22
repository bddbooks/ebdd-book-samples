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

import java.time.LocalTime;
import java.util.Comparator;
import java.util.Optional;

public class OrderService {
    public static Order placeOrder(String customerName, String pizzaName, LocalTime placingTime) {
        return placeOrder(customerName, pizzaName, placingTime, null);
    }

    public static Order placeOrder(String customerName, String pizzaName, LocalTime placingTime, Integer forcedOrderNo) {
        if (!AuthenticationService.isAuthenticated(customerName)) {
            throw new RuntimeException("Customer " + customerName + " is not authenticated.");
        }

        int orderNo = Optional.ofNullable(forcedOrderNo).orElse(DataRepository.INSTANCE.getNextOrderNo());
        Order order = new Order(orderNo, customerName, pizzaName, placingTime);
        setStatus(order, OrderStatus.PLACED);
        DataRepository.INSTANCE.saveOrder(order);
        return order;
    }

    private static void setStatus(Order order, OrderStatus status) {
        order.setStatus(status);
    }

    public static Order getOrder(int orderNo) {
        return DataRepository.INSTANCE.getOrderByOrderNr(orderNo);
    }

    public static Order startWorkOnNextOrder()
    {
        var nextOrder = DataRepository.INSTANCE.getPlacedOrders()
            .stream().min(Comparator.comparing(Order::getPlacingTime));

        if (nextOrder.isEmpty())
        {
            return null;
        }

        setStatus(nextOrder.get(), OrderStatus.IN_PREPARATION);
        return nextOrder.get();
    }
}
