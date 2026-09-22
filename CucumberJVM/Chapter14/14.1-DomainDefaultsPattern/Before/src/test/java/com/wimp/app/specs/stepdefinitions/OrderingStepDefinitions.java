package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.Order;
import com.wimp.app.services.AuthenticationService;
import com.wimp.app.services.EmailService;
import com.wimp.app.services.OrderService;
import com.wimp.app.services.PromotionService;
import com.wimp.app.specs.support.OrderingContext;
import io.cucumber.java.en.*;

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
        var order = new Order();
        order.addItem(pizzaName, "Medium");  // Hard-coded default value

        AuthenticationService.login("Rebecca");
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
        assertTrue(emailService.wasCouponSent("Rebecca", couponCode));
    }
}
