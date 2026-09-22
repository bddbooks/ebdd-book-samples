package com.wimp.app.specs.drivers;

import com.wimp.app.models.Order;
import com.wimp.app.models.OrderStatus;
import com.wimp.app.restapi.PlaceOrderRequest;
import com.wimp.app.specs.support.LambdaAction;
import com.wimp.app.specs.support.TestAction;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@Component
@Profile("backdoor-api")
public class BackdoorApiDriver {
    private final BackdoorApiClient backdoorApiClient;

    @HttpExchange("/api/test")
    public interface BackdoorApiClient {
        @PostExchange("/prepare-order")
        Order prepareOrder(@RequestParam String customerName, @RequestParam OrderStatus status,
                           @RequestBody PlaceOrderRequest request);

    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public BackdoorApiDriver(BackdoorApiClient backdoorApiClient) {
        this.backdoorApiClient = backdoorApiClient;
    }

    public TestAction<Order> prepareOrder(String customerName, PlaceOrderRequest orderRequest, OrderStatus status) {
        return new LambdaAction<>("Prepare test order", "%s/%s/%s".formatted(customerName, orderRequest, status), () ->
            backdoorApiClient.prepareOrder(customerName, status, orderRequest));
    }
}
