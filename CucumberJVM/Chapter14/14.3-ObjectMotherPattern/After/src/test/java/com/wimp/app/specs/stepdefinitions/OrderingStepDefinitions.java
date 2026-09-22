package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.OrderStatus;
import com.wimp.app.services.AuthenticationService;
import com.wimp.app.services.EmailService;
import com.wimp.app.services.OrderService;
import com.wimp.app.services.PromotionService;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.OrderObjectMother;
import com.wimp.app.specs.support.OrderingContext;
import io.cucumber.java.en.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OrderingStepDefinitions {
    private final OrderingContext orderingContext;
    private final OrderService orderService;
    private final PromotionService promotionService;
    private final EmailService emailService;

    public OrderingStepDefinitions(OrderingContext orderingContext, OrderService orderService, PromotionService promotionService, EmailService emailService) {
        this.orderingContext = orderingContext;
        this.orderService = orderService;
        this.promotionService = promotionService;
        this.emailService = emailService;
    }

    @Given("the customer has placed an order containing a {string} pizza")
    public void theCustomerHasPlacedAnOrderContainingAPizza(String pizzaName) {
        var order = new OrderObjectMother().withItem(pizzaName).build();

        AuthenticationService.login(DomainDefaults.CUSTOMER_NAME);
        var placedOrder = orderService.placeOrder(order);
        orderingContext.setCurrentOrderNo(placedOrder.getOrderNo());
    }

    @When("the customer places an order for {int} pizza(s) of size {string}")
    public void theCustomerPlacesAnOrderForPizzasOfSize(int count, String pizzaSize) {
        var order = new OrderObjectMother().withItems(count, pizzaSize).build();

        AuthenticationService.login(DomainDefaults.CUSTOMER_NAME);
        var placedOrder = orderService.placeOrder(order);
        orderingContext.setCurrentOrderNo(placedOrder.getOrderNo());
    }


    @Given("the customer has placed an order")
    public void theCustomerHasPlacedAnOrder() {
        var order = new OrderObjectMother().build();

        AuthenticationService.login(DomainDefaults.CUSTOMER_NAME);
        var placedOrder = orderService.placeOrder(order);
        orderingContext.setCurrentOrderNo(placedOrder.getOrderNo());
    }

    @Given("the {string} promotion is active")
    public void thePromotionIsActive(String promotionName) {
        promotionService.activatePromotion(promotionName);
    }

    @When("the order is delivered")
    public void theOrderIsDelivered() {
        orderService.deliverOrder(orderingContext.getCurrentOrderNoVerified());
    }

    @Then("the customer should receive a {string} coupon via email")
    public void theCustomerShouldReceiveACouponViaEmail(String couponCode) {
        assertTrue(emailService.wasCouponSent(DomainDefaults.CUSTOMER_NAME, couponCode));
    }

    @Then("the order should be rejected")
    public void theOrderShouldBeRejected() {
        int orderNo = orderingContext.getCurrentOrderNoVerified();
        var order = Optional.of(orderService.getOrder(orderNo))
            .orElseThrow(() -> new RuntimeException("Order not found"));
        assertEquals(OrderStatus.REJECTED, order.getStatus());
    }

    @And("the order is waiting for pickup")
    public void theOrderIsWaitingForPickup() {
        int orderNo = orderingContext.getCurrentOrderNoVerified();
        orderService.setWaitingForPickup(orderNo);
    }

    private Exception deliveryAddressChangeError;

    @When("the customer requests to change the delivery address")
    public void theCustomerRequestsToChangeTheDeliveryAddress() {
        int orderNo = orderingContext.getCurrentOrderNoVerified();
        try
        {
            deliveryAddressChangeError = null;
            orderService.changeDeliveryAddress(orderNo, DomainDefaults.ALT_DELIVERY_ADDRESS);
        }
        catch (Exception ex)
        {
            deliveryAddressChangeError = ex;
        }
    }

    @Then("the address change should be allowed")
    public void theAddressChangeShouldBeAllowed() {
        assertNull(deliveryAddressChangeError);
    }
}
