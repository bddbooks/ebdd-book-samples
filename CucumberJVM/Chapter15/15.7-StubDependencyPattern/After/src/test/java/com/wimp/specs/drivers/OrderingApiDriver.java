package com.wimp.specs.drivers;

import com.wimp.app.restapi.OrderResponse;
import com.wimp.app.restapi.PlaceOrderRequest;
import com.wimp.specs.support.LambdaAction;
import com.wimp.specs.support.RestApiContext;
import com.wimp.specs.support.TestAction;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class OrderingApiDriver {
    private final RestApiContext restApiContext;

    public OrderingApiDriver(RestApiContext restApiContext) {
        this.restApiContext = restApiContext;
    }

    public TestAction<OrderResponse> placeOrder(PlaceOrderRequest placeOrderRequest) {
        return new LambdaAction<>(() -> restApiContext.processRequest(
            "Place order",
            HttpMethod.POST,
            "/api/orders",
            placeOrderRequest,
            HttpStatus.CREATED,
            OrderResponse.class));
    }
}
