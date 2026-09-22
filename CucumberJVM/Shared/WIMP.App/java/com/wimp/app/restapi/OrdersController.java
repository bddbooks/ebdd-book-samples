/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

package com.wimp.app.restapi;

import com.wimp.app.models.ContactDetails;
import com.wimp.app.models.Order;
import com.wimp.app.models.ServiceResult;
import com.wimp.app.services.AuthenticationService;
import com.wimp.app.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@Transactional
public class OrdersController {
    private final OrderService orderService;
    private final AuthenticationService authenticationService;

    public OrdersController(OrderService orderService, AuthenticationService authenticationService) {
        this.orderService = orderService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> placeOrder(
        @RequestBody PlaceOrderRequest request,
        @RequestHeader("Authorization") String authorization
    ) {
        String customerName = requireAuthenticatedCustomer(authorization);
        ServiceResult<Order> result = orderService.placeOrder(customerName, request);
        if (!result.successful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.errorMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.value());
    }

    @GetMapping("/orders/{orderNo}")
    public ResponseEntity<?> getOrder(@PathVariable int orderNo) {
        Order order = orderService.getOrder(orderNo);
        return order == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(order);
    }

    @DeleteMapping("/orders/{orderNo}")
    public ResponseEntity<Void> cancelOrder(
        @PathVariable int orderNo,
        @RequestHeader("Authorization") String authorization
    ) {
        String customerName = requireAuthenticatedCustomer(authorization);
        ServiceResult<Order> result = orderService.cancelOrder(orderNo, customerName);
        if (!result.successful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.errorMessage());
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/orders/{orderNo}/deliver")
    public ResponseEntity<?> deliverOrder(@PathVariable int orderNo) {
        ServiceResult<Order> result = orderService.deliverOrder(orderNo);
        if (!result.successful()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(result.value());
    }

    @PutMapping("/orders/{orderNo}/delivery-address")
    public ResponseEntity<Void> changeDeliveryAddress(
        @PathVariable int orderNo,
        @RequestBody ChangeAddressRequest request
    ) {
        ServiceResult<Order> result = orderService.changeDeliveryAddress(orderNo, request.address());
        if (!result.successful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.errorMessage());
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/orders/{orderNo}/customer-collection")
    public ResponseEntity<?> setForCustomerCollection(
        @PathVariable int orderNo,
        @RequestHeader("Authorization") String authorization
    ) {
        String customerName = requireAuthenticatedCustomer(authorization);
        ServiceResult<Order> result = orderService.setForCustomerCollection(orderNo, customerName);
        if (!result.successful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.errorMessage());
        }

        return ResponseEntity.ok(result.value().getOrderCollectionDetails());
    }

    @PutMapping("/orders/{orderNo}/contact-details")
    public ResponseEntity<?> provideContactDetails(
        @PathVariable int orderNo,
        @RequestBody ContactDetails contactDetails,
        @RequestHeader("Authorization") String authorization
    ) {
        requireAuthenticatedCustomer(authorization);
        ServiceResult<Order> result = orderService.provideContactDetails(orderNo, contactDetails);
        if (!result.successful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.errorMessage());
        }

        return ResponseEntity.ok(result.value());
    }

    @PostMapping("/kitchen/ready-for-pickup/{orderNo}")
    public ResponseEntity<Void> readyForPickup(@PathVariable int orderNo) {
        ServiceResult<Order> result = orderService.setWaitingForPickup(orderNo);
        if (!result.successful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.errorMessage());
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/kitchen/take-next-order")
    public ResponseEntity<?> takeNextOrder(@RequestHeader("Authorization") String authorization) {
        String customerName = requireAuthenticatedCustomer(authorization);
        if (!customerName.startsWith("Kitchen")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The request is not authorized for this operation");
        }

        Order order = orderService.takeNextOrder();
        return order == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(order);
    }

    private String requireAuthenticatedCustomer(String authorization) {
        String token = authenticationService.extractToken(authorization);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The request is not authorized for this operation");
        }

        String customerName = authenticationService.getCustomerName(token);
        if (customerName == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The request is not authorized for this operation");
        }

        return customerName;
    }
}
