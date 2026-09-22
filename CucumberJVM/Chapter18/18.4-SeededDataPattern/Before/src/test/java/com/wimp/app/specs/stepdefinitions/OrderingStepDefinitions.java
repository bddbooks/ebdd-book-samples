package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.specs.drivers.MenuBackdoorDriver;
import com.wimp.app.specs.drivers.OrderingApiDriver;
import com.wimp.app.specs.support.AuthenticationContext;
import com.wimp.app.specs.support.MenuItemData;
import com.wimp.app.specs.support.OrderingContext;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.java.en.*;

public class OrderingStepDefinitions {
    private final OrderingContext orderingContext;
    private final AuthenticationContext authenticationContext;
    private final OrderingApiDriver orderingApiDriver;
    private final MenuBackdoorDriver menuBackdoorDriver;

    public OrderingStepDefinitions(OrderingContext orderingContext, AuthenticationContext authenticationContext, OrderingApiDriver orderingApiDriver, MenuBackdoorDriver menuBackdoorDriver) {
        this.orderingContext = orderingContext;
        this.authenticationContext = authenticationContext;
        this.orderingApiDriver = orderingApiDriver;
        this.menuBackdoorDriver = menuBackdoorDriver;
    }

    @Given("the customer has placed an order containing a {string} pizza")
    public void theCustomerHasPlacedAnOrderContainingAPizza(String pizzaName) throws Exception {
        // ensuring that the pizza is on the menu
        var menu = menuBackdoorDriver.getMenuItems();
        if (menu.stream().noneMatch(mi -> mi.getName().equals(pizzaName))) {
            menuBackdoorDriver.addMenuItem(new MenuItemData(pizzaName));
        }

        authenticationContext.ensureAuthenticatedCustomer();
        var placeOrderRequest = new PlaceOrderRequestObjectMother().withItem(pizzaName).build();
        var placedOrder = orderingApiDriver.placeOrder(placeOrderRequest).execute();
        orderingContext.setCurrentOrderNo(placedOrder.getOrderNo());
    }

    @When("the order is delivered")
    public void theOrderIsDelivered() throws Exception {
        orderingApiDriver.deliverOrder(orderingContext.getCurrentOrderNoVerified()).execute();
    }
}
