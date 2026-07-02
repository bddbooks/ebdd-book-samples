package com.wimp.app.services;

import com.wimp.app.data.InMemoryDataStore;
import com.wimp.app.models.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final InMemoryDataStore dataStore;

    public NotificationService(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void sendDelayNotification(String customerName) {
        dataStore.saveNotification(customerName, "Your order is delayed.");
    }

    public List<Notification> getNotifications(String customerName) {
        return dataStore.getNotifications(customerName);
    }
}
