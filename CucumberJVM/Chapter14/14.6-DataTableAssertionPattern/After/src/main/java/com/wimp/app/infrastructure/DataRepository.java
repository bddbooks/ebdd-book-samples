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

import com.wimp.app.models.CouponEmail;
import com.wimp.app.models.MenuItem;
import com.wimp.app.models.Order;
import com.wimp.app.models.Promotion;

import java.util.Collection;
import java.util.List;

public interface DataRepository {
    int getNextOrderNo();

    Order getOrderByOrderNr(int orderNr);

    List<Order> getPlacedOrders();

    void saveOrder(Order order);

    Promotion getPromotion(String promotionName);

    void savePromotion(Promotion promotion);

    void insertCouponEmail(CouponEmail couponEmail);

    List<CouponEmail> getCouponEmailsByCustomer(String customerName);

    void saveMenuItem(MenuItem menuItem);

    List<MenuItem> getMenuItems();
}
