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

import com.wimp.app.models.Order;
import com.wimp.app.models.OrderStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple simulation of a database context, using in-memory collections. In a
 * real application, this would likely be replaced with an actual database
 * context or a repository pattern.
 */
public class DataRepository {
    public static final DataRepository INSTANCE = new DataRepository();

    private int nextOrderNo = 1;
    private final Map<Integer, Order> orders = new HashMap<>();

    public void reset() {
        orders.clear();
        nextOrderNo = 1;
    }

    public int getNextOrderNo() {
        return nextOrderNo++;
    }

    public Order getOrderByOrderNr(int orderNr) {
        return orders.values().stream()
            .filter(o -> o.getOrderNo() == orderNr)
            .findFirst()
            .orElse(null);
    }

    public List<Order> getPlacedOrders() {
        return orders.values().stream()
            .filter(o -> o.getStatus() == OrderStatus.PLACED)
            .toList();
    }

    public void saveOrder(Order order) {
        orders.put(order.getOrderNo(), order);
    }
}
