package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.Order;
import com.wimp.app.models.PizzaSize;
import com.wimp.app.specs.drivers.OrderingApiDriver;
import com.wimp.app.specs.support.AuthenticationContext;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import com.wimp.app.specs.support.TestActionResult;
import io.cucumber.java.en.*;

public class OrderingStepDefinitions {
    private final OrderingApiDriver orderingApiDriver;
    private final AuthenticationContext authenticationContext;

    private TestActionResult<Order> placeOrderResult = TestActionResult.notExecuted();

    public OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, AuthenticationContext authenticationContext) {
        this.orderingApiDriver = orderingApiDriver;
        this.authenticationContext = authenticationContext;
    }

    @When("the customer places an order for {int} pizzas of size {pizza-size}")
    public void theCustomerPlacesAnOrderForPizzasOfSize(int count, PizzaSize size) throws Exception {
        authenticationContext.ensureAuthenticatedCustomer();

        var orderRequest = new PlaceOrderRequestObjectMother()
            .withItems(count, size)
            .build();
        placeOrderResult = orderingApiDriver.placeOrder(orderRequest).attemptExecute();
    }

    @Then("the order should be rejected with message {user-message}")
    public void theOrderShouldBeRejectedWithMessage(String expectedMessage) {
        placeOrderResult.assertFailedWithErrorMessageContains(expectedMessage);
    }
}
