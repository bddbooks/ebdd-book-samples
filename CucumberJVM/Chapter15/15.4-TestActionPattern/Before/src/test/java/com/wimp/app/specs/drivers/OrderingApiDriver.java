package com.wimp.app.specs.drivers;

import com.wimp.app.models.Order;
import com.wimp.app.restapi.PlaceOrderRequest;
import com.wimp.app.specs.support.TestActionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@Component
public class OrderingApiDriver {

    protected static final Logger log = LoggerFactory.getLogger(OrderingApiDriver.class);

    private final OrderingApiClient orderingApiClient;

    @HttpExchange("/api/orders")
    public interface OrderingApiClient {
        @PostExchange
        Order placeOrder(@RequestBody PlaceOrderRequest request);

        @DeleteExchange("/{orderNo}")
        void cancelOrder(@PathVariable int orderNo);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public OrderingApiDriver(OrderingApiClient orderingApiClient) {
        this.orderingApiClient = orderingApiClient;
    }

    public Order placeOrder(PlaceOrderRequest placeOrderRequest) {
        log.info("Executing Place order...");
        long startTime = System.nanoTime();
        try {
            var response = orderingApiClient.placeOrder(placeOrderRequest);
            log.info("Place order executed successfully in {}", (System.nanoTime() - startTime) / 1_000_000);
            return response;
        } catch (Exception ex) {
            log.error("Place order failed: {}", ex.getMessage());
            throw new TestActionFailedException(ex.getMessage());
        }
    }

    public void cancelOrder(int orderNo) {
        log.info("Executing Cancel order...");
        long startTime = System.nanoTime();
        try {
            orderingApiClient.cancelOrder(orderNo);
            log.info("Cancel order executed successfully in {}", (System.nanoTime() - startTime) / 1_000_000);
        } catch (Exception ex) {
            log.error("Cancel order failed: {}", ex.getMessage());
            throw new TestActionFailedException(ex.getMessage());
        }
    }
}
