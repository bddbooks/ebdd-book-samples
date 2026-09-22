package com.wimp.app.specs.drivers;

import com.wimp.app.models.Order;
import com.wimp.app.restapi.PlaceOrderRequest;
import com.wimp.app.specs.support.LambdaAction;
import com.wimp.app.specs.support.TestAction;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@Component
public class OrderingApiDriver {
    private final OrderingApiClient orderingApiClient;

    @HttpExchange("/api/orders")
    public interface OrderingApiClient {
        @PostExchange
        Order placeOrder(@RequestBody PlaceOrderRequest request);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public OrderingApiDriver(OrderingApiClient orderingApiClient) {
        this.orderingApiClient = orderingApiClient;
    }

    public TestAction<Order> placeOrder(PlaceOrderRequest placeOrderRequest) {
        return new LambdaAction<>("Place order", placeOrderRequest, () -> orderingApiClient.placeOrder(placeOrderRequest));
    }
}
