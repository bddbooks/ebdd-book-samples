package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.specs.drivers.BackdoorApiDriver;
import com.wimp.app.specs.drivers.OrderingApiDriver;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.NamedOrderData;
import com.wimp.app.specs.support.OrderingContext;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.java.en.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderingStepDefinitions {
    private final OrderingContext orderingContext;
    private final OrderingApiDriver orderingApiDriver;
    private final BackdoorApiDriver backdoorApiDriver;

    public OrderingStepDefinitions(OrderingContext orderingContext, OrderingApiDriver orderingApiDriver, BackdoorApiDriver backdoorApiDriver) {
        this.orderingContext = orderingContext;
        this.orderingApiDriver = orderingApiDriver;
        this.backdoorApiDriver = backdoorApiDriver;
    }

    @Given("the customer has the following orders")
    public void theCustomerHasTheFollowingOrders(List<NamedOrderData> orders) throws Exception {
        for (final NamedOrderData order : orders) {
            var orderRequest = new PlaceOrderRequestObjectMother().build();
            var placedOrder = backdoorApiDriver
                .prepareOrder(DomainDefaults.CUSTOMER_NAME, orderRequest, order.getStatus())
                .execute();
            orderingContext.getNamedOrders().put(order.getOrderName(), placedOrder.getOrderNo());
        }
    }

    @When("they cancel {order}")
    public void theyCancelOrder(int orderNo) throws Exception {
        orderingApiDriver.cancelOrder(orderNo).execute();
    }

    @Then("their order list should contain")
    public void thenTheirOrderListShouldContain(List<NamedOrderData> expectedOrders) throws Exception {
        for (final NamedOrderData expectedOrder : expectedOrders) {
            var orderNo = orderingContext.getNamedOrders().getOrDefault(expectedOrder.getOrderName(), null);
            if (orderNo == null) {
                throw new RuntimeException("Order %s not known".formatted(expectedOrder.getOrderName()));
            }

            var order = orderingApiDriver.getOrder(orderNo).execute();
            assertEquals(expectedOrder.getStatus(), order.getStatus(), "Unexpected status for order %s.".formatted(expectedOrder.getOrderName()));
        }
    }
}
