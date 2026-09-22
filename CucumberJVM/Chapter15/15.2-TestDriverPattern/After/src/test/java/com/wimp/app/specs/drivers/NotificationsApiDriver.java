package com.wimp.app.specs.drivers;

import com.wimp.app.models.Notification;
import com.wimp.app.specs.support.TestActionFailedException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

@Component
public class NotificationsApiDriver {

    private final RestTestClient restTestClient;

    public NotificationsApiDriver(WebApplicationContext context) {
        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
    }

    public List<Notification> getNotifications(String customerName) {
        try {
            var response = restTestClient.get().uri("/api/notifications/{customerName}", customerName)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
            response.expectStatus().is2xxSuccessful();
            return Optional.ofNullable(response.expectBody(new ParameterizedTypeReference<List<Notification>>() {}).returnResult().getResponseBody())
                .orElseThrow(() -> new RuntimeException("No result payload found"));
        } catch (Exception ex) {
            throw new TestActionFailedException(ex.getMessage());
        }
    }
}
