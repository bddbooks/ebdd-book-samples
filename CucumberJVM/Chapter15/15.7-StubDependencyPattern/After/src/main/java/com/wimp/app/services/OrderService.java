package com.wimp.app.services;

import com.wimp.app.data.InMemoryDataStore;
import com.wimp.app.models.Order;
import com.wimp.app.models.OrderStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {
    private static final int DEFAULT_DELIVERY_MINUTES = 45;

    private final InMemoryDataStore dataStore;
    private final NotificationService notificationService;
    private final TimeService timeService;

    public OrderService(InMemoryDataStore dataStore, NotificationService notificationService, TimeService timeService) {
        this.dataStore = dataStore;
        this.notificationService = notificationService;
        this.timeService = timeService;
    }

    public Order placeOrder(String customerName, LocalDateTime expectedDeliveryTime) {
        LocalDateTime placingTime = timeService.getCurrentTime();
        LocalDateTime effectiveExpectedDeliveryTime = expectedDeliveryTime != null
            ? expectedDeliveryTime
            : placingTime.plusMinutes(DEFAULT_DELIVERY_MINUTES);

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setExpectedDeliveryTime(effectiveExpectedDeliveryTime);
        order.setStatus(OrderStatus.PLACED);
        order = dataStore.insertOrder(order);

        subscribeForDelayNotification(order);
        return order;
    }

    private void subscribeForDelayNotification(Order order) {
        timeService.subscribeToTimeChange(time -> {
            if (time.isBefore(order.getExpectedDeliveryTime())) {
                return false;
            }

            Order currentOrder = dataStore.getOrderByOrderNo(order.getOrderNo());
            if (currentOrder != null && currentOrder.getStatus() != OrderStatus.COMPLETED) {
                notificationService.sendDelayNotification(order.getCustomerName());
            }
            return true;
        });
    }
}
