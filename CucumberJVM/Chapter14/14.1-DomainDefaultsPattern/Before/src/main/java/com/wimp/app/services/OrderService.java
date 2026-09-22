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
        setStatus(order, OrderStatus.PLACED);
        dataRepository.saveOrder(order);
        return order;
    }

    private static void setStatus(Order order, OrderStatus status) {
        order.setStatus(status);
    }

    public Order getOrder(int orderNo) {
        return dataRepository.getOrderByOrderNr(orderNo);
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

        setStatus(order, OrderStatus.COMPLETED);

        // Check if order contains Margherita pizza and Margherita Friday promotion is active
        if (order.getItems().stream().anyMatch(i -> i.name().equals("Margherita")) &&
            promotionService.isPromotionActive("Margherita Friday"))
        {
            emailService.sendCouponEmail(order.getCustomerName(), "MARGHERITA25");
        }
    }
}
