package com.wimp.app.specs.drivers;

import com.wimp.app.models.Order;
import com.wimp.app.restapi.PlaceOrderRequest;
import com.wimp.app.specs.support.LambdaAction;
import com.wimp.app.specs.support.TestAction;
import com.wimp.app.specs.support.VoidReturn;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.GetExchange;

@Component
public class OrderingApiDriver {
    private final OrderingApiClient orderingApiClient;

    @HttpExchange("/api/orders")
    public interface OrderingApiClient {
        @GetExchange("/{orderNo}")
        Order getOrder(@PathVariable int orderNo);

        @PostExchange
        Order placeOrder(@RequestBody PlaceOrderRequest request);

        @DeleteExchange("/{orderNo}")
        void cancelOrder(@PathVariable int orderNo);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public OrderingApiDriver(OrderingApiClient orderingApiClient) {
        this.orderingApiClient = orderingApiClient;
    }

    public TestAction<Order> getOrder(int orderNo) {
        return new LambdaAction<>("Get order", orderNo, () -> orderingApiClient.getOrder(orderNo));
    }

    public TestAction<Order> placeOrder(PlaceOrderRequest placeOrderRequest) {
        return new LambdaAction<>("Place order", placeOrderRequest, () -> orderingApiClient.placeOrder(placeOrderRequest));
    }

    public TestAction<VoidReturn> cancelOrder(int orderNo) {
        return new LambdaAction.Void("Cancel order", orderNo, () ->
            orderingApiClient.cancelOrder(orderNo));
    }
}
