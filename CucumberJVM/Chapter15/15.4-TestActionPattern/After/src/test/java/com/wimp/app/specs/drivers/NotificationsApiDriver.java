package com.wimp.app.specs.drivers;

import com.wimp.app.models.Notification;
import com.wimp.app.specs.support.LambdaAction;
import com.wimp.app.specs.support.TestAction;
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

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public NotificationsApiDriver(NotificationsApiClient notificationsApiClient) {
        this.notificationsApiClient = notificationsApiClient;
    }

    public TestAction<List<Notification>> getNotifications(String customerName) {
        return new LambdaAction<>("Get notifications", customerName,
            () -> List.of(notificationsApiClient.getNotifications(customerName)));
    }
}
