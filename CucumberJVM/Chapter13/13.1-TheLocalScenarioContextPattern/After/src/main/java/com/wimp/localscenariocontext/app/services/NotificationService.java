package com.wimp.localscenariocontext.app.services;

import com.wimp.localscenariocontext.app.infrastructure.DataContext;
import com.wimp.localscenariocontext.app.models.Notification;

public class NotificationService {
    public static void sendCancellationNotification(String customerName) {
        DataContext.INSTANCE.saveNotification(new Notification(customerName, "Your order has been cancelled."));
    }

    public static boolean wasNotificationSent(String customerName) {
        return !DataContext.INSTANCE.getNotificationsByCustomerName(customerName).isEmpty();
    }
}
