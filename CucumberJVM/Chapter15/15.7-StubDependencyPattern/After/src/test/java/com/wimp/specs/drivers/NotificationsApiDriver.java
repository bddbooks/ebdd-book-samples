package com.wimp.specs.drivers;

import com.wimp.app.models.Notification;
import com.wimp.specs.support.LambdaAction;
import com.wimp.specs.support.RestApiContext;
import com.wimp.specs.support.TestAction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationsApiDriver {
    private final RestApiContext restApiContext;

    public NotificationsApiDriver(RestApiContext restApiContext) {
        this.restApiContext = restApiContext;
    }

    public TestAction<List<Notification>> getNotifications(String customerName) {
        return new LambdaAction<>(() -> List.of(restApiContext.getRequest(
            "/api/notifications/" + customerName,
            Notification[].class)));
    }
}
