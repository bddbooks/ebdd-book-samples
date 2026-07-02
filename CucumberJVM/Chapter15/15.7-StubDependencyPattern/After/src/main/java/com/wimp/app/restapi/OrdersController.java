package com.wimp.app.restapi;

import com.wimp.app.models.Order;
import com.wimp.app.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {
    private final OrderService orderService;

    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
        @RequestHeader("Authorization") String authorization,
        @RequestBody PlaceOrderRequest request
    ) {
        String customerName = getCustomerNameFromBearerToken(authorization);
        Order order = orderService.placeOrder(customerName, request.expectedDeliveryTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrderResponse(order.getOrderNo()));
    }

    private String getCustomerNameFromBearerToken(String authorization) {
        if (!authorization.startsWith("Bearer token-")) {
            throw new IllegalArgumentException("Invalid bearer token.");
        }
        return authorization.substring("Bearer token-".length());
    }
}
