package com.wimp.app.specs.support;

import com.wimp.app.infrastructure.DataRepository;
import com.wimp.app.models.*;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple sub implementation of a database context, using in-memory
 * collections. This approach is effective for simple cases. Check out samples
 * of chapter 15 or later where we use H2 in-memory database as stub at the
 * Spring database layer.
 */
@Component
@ScenarioScope
public class StubDataRepository implements DataRepository {
    private int nextOrderNo = 1;
    private final Map<Integer, Order> orders = new HashMap<>();
    private final List<Promotion> promotions = new ArrayList<>();
    private final List<CouponEmail> couponEmails = new ArrayList<>();
    private final List<MenuItem> menuItems = new ArrayList<>();

    @Override
    public int getNextOrderNo() {
        return nextOrderNo++;
    }

    @Override
    public Order getOrderByOrderNr(int orderNr) {
        return orders.values().stream()
            .filter(o -> o.getOrderNo() == orderNr)
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<Order> getPlacedOrders() {
        return orders.values().stream()
            .filter(o -> o.getStatus() == OrderStatus.PLACED)
            .toList();
    }

    @Override
    public void saveOrder(Order order) {
        orders.put(order.getOrderNo(), order);
    }

    @Override
    public Promotion getPromotion(String promotionName) {
        return promotions.stream()
            .filter(o -> o.promotionName().equals(promotionName))
            .findFirst()
            .orElse(null);
    }

    @Override
    public void savePromotion(Promotion promotion) {
        promotions.add(promotion);
    }

    @Override
    public void insertCouponEmail(CouponEmail couponEmail) {
        couponEmails.add(couponEmail);
    }

    @Override
    public List<CouponEmail> getCouponEmailsByCustomer(String customerName) {
        return couponEmails.stream()
            .filter(o -> o.customerName().equals(customerName))
            .toList();
    }

    @Override
    public void saveMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
    }

    @Override
    public List<MenuItem> getMenuItems() {
        return new ArrayList<>(menuItems);
    }
}
