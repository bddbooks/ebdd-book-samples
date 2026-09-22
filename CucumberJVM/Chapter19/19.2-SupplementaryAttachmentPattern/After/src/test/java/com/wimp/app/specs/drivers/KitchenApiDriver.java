package com.wimp.app.specs.drivers;

import com.wimp.app.models.Order;
import com.wimp.app.models.OrderCollectionDetails;
import com.wimp.app.specs.support.LambdaAction;
import com.wimp.app.specs.support.TestAction;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@Component
public class KitchenApiDriver {
    private final KitchenApiClient kitchenApiClient;

    @HttpExchange("/api/kitchen")
    public interface KitchenApiClient {
        @PostExchange("/take-next-order")
        Order takeNextOrder();

        @PostExchange("/ready-for-pickup/{orderNo}")
        OrderCollectionDetails setReady(@PathVariable int orderNo);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public KitchenApiDriver(KitchenApiClient kitchenApiClient) {
        this.kitchenApiClient = kitchenApiClient;
    }

    public TestAction<Order> takeNextOrder() {
        return new LambdaAction<>("Take next order", kitchenApiClient::takeNextOrder);
    }

    public TestAction<OrderCollectionDetails> setReady(int orderNo) {
        return new LambdaAction<>("Set order as ready", orderNo, () -> kitchenApiClient.setReady(orderNo));
    }
}
