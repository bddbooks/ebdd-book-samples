package com.wimp.app.services;

import com.wimp.app.infrastructure.DataContext;
import com.wimp.app.models.Notification;

public class NotificationService {
    public static void sendCancellationNotification(String customerName) {
        DataContext.INSTANCE.saveNotification(new Notification(customerName, "Your order has been cancelled."));
    }

    public static boolean wasNotificationSent(String customerName) {
        return !DataContext.INSTANCE.getNotificationsByCustomerName(customerName).isEmpty();
    }
}
