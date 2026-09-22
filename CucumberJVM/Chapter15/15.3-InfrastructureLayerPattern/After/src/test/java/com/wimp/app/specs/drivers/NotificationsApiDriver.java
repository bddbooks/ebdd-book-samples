package com.wimp.app.specs.drivers;

import com.wimp.app.models.Notification;
import com.wimp.app.specs.support.TestActionFailedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@Component
public class NotificationsApiDriver {

    private final NotificationsApiClient notificationsApiClient;

    @HttpExchange("/api/notifications")
    public interface NotificationsApiClient {
        @GetExchange("/{customerName}")
        Notification[] getNotifications(@PathVariable String customerName);
    }

    public NotificationsApiDriver(NotificationsApiClient notificationsApiClient) {
        this.notificationsApiClient = notificationsApiClient;
    }

    public List<Notification> getNotifications(String customerName) {
        try {
            return List.of(notificationsApiClient.getNotifications(customerName));
        } catch (Exception ex) {
            throw new TestActionFailedException(ex.getMessage());
        }
    }
}
