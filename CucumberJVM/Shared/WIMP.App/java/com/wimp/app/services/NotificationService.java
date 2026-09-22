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

import com.wimp.app.data.DataRepository;
import com.wimp.app.models.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {
    private final DataRepository repository;

    public NotificationService(DataRepository repository) {
        this.repository = repository;
    }

    public void sendCancellationNotification(String customerName) {
        repository.insertNotification(new Notification(customerName, "Your order has been cancelled."));
    }

    @Transactional // delay notifications are sent from a background thread without implicit transaction context. This annotation creates a transaction boundary for inserting the notification.
    public void sendDelayNotification(String customerName) {
        repository.insertNotification(new Notification(customerName, "Your order has been delayed."));
    }

    public List<Notification> getNotifications(String customerName) {
        return repository.getNotificationsByCustomerName(customerName);
    }
}
