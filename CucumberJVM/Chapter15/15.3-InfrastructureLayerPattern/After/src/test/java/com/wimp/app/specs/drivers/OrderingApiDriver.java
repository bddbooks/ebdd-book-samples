package com.wimp.app.specs.drivers;

import com.wimp.app.models.Order;
import com.wimp.app.restapi.PlaceOrderRequest;
import com.wimp.app.specs.support.TestActionFailedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@Component
public class OrderingApiDriver {

    private final OrderingApiClient orderingApiClient;

    @HttpExchange("/api/orders")
    public interface OrderingApiClient {
        @PostExchange
        Order placeOrder(@RequestBody PlaceOrderRequest request);

        @DeleteExchange("/{orderNo}")
        void cancelOrder(@PathVariable int orderNo);
    }

    public OrderingApiDriver(OrderingApiClient orderingApiClient) {
        this.orderingApiClient = orderingApiClient;
    }

    public Order placeOrder(PlaceOrderRequest placeOrderRequest) {
        try {
            return orderingApiClient.placeOrder(placeOrderRequest);
        } catch (Exception ex) {
            throw new TestActionFailedException(ex.getMessage());
        }
    }

    public void cancelOrder(int orderNo) {
        try {
            orderingApiClient.cancelOrder(orderNo);
        } catch (Exception ex) {
            throw new TestActionFailedException(ex.getMessage());
        }
    }
}
