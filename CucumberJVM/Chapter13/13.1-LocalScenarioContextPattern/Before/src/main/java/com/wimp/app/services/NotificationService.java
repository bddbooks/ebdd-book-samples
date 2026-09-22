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
import com.wimp.app.models.Notification;

public class NotificationService {
    public static void sendCancellationNotification(String customerName) {
        DataRepository.INSTANCE.saveNotification(new Notification(customerName, "Your order has been cancelled."));
    }

    public static boolean wasNotificationSent(String customerName) {
        return !DataRepository.INSTANCE.getNotificationsByCustomerName(customerName).isEmpty();
    }
}
