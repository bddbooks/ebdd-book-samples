package com.wimp.app.specs.drivers;

import com.wimp.app.models.Notification;
import com.wimp.app.specs.support.TestActionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@Component
public class NotificationsApiDriver {

    protected static final Logger log = LoggerFactory.getLogger(NotificationsApiDriver.class);

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

    public List<Notification> getNotifications(String customerName) {
        log.info("Executing Get notifications...");
        long startTime = System.nanoTime();
        try {
            var response = List.of(notificationsApiClient.getNotifications(customerName));
            log.info("Get notifications executed successfully in {}", (System.nanoTime() - startTime) / 1_000_000);
            return response;
        } catch (Exception ex) {
            log.error("Get notifications failed: {}", ex.getMessage());
            throw new TestActionFailedException(ex.getMessage());
        }
    }
}
