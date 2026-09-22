package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.OrderStatus;
import com.wimp.app.models.PizzaSize;
import com.wimp.app.services.AuthenticationService;
import com.wimp.app.services.OrderService;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.OrderObjectMother;
import com.wimp.app.specs.support.OrderingContext;
import io.cucumber.java.en.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OrderingStepDefinitions {
    private final OrderingContext orderingContext;
    private final OrderService orderService;

    public OrderingStepDefinitions(OrderingContext orderingContext, OrderService orderService) {
        this.orderingContext = orderingContext;
        this.orderService = orderService;
    }

    @When("the customer places an order for {int} pizza(s) of size {string}")
    public void theCustomerPlacesAnOrderForPizzasOfSize(int count, String pizzaSize) {
        var order = new OrderObjectMother().withItems(count, PizzaSize.valueOf(pizzaSize.toUpperCase())).build();

        AuthenticationService.login(DomainDefaults.CUSTOMER_NAME);
        var placedOrder = orderService.placeOrder(order);
        orderingContext.setCurrentOrderNo(placedOrder.getOrderNo());
    }

    @Then("the order should be rejected")
    public void theOrderShouldBeRejected() {
        int orderNo = orderingContext.getCurrentOrderNoVerified();
        var order = Optional.of(orderService.getOrder(orderNo))
            .orElseThrow(() -> new RuntimeException("Order not found"));
        assertEquals(OrderStatus.REJECTED, order.getStatus());
    }
}
