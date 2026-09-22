package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.specs.drivers.BackdoorApiDriver;
import com.wimp.app.specs.drivers.OrderingApiDriver;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.OrderByNumberData;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.java.en.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderingStepDefinitions {
    private final OrderingApiDriver orderingApiDriver;
    private final BackdoorApiDriver backdoorApiDriver;

    public OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, BackdoorApiDriver backdoorApiDriver) {
        this.orderingApiDriver = orderingApiDriver;
        this.backdoorApiDriver = backdoorApiDriver;
    }

    @Given("the customer has the following orders")
    public void theCustomerHasTheFollowingOrders(List<OrderByNumberData> orders) throws Exception {
        for (final OrderByNumberData order : orders) {
            var orderRequest = new PlaceOrderRequestObjectMother().build();
            backdoorApiDriver
                .prepareOrder(DomainDefaults.CUSTOMER_NAME, orderRequest, order.getStatus())
                .execute();
        }
    }

    @When("they cancel {order}")
    public void theyCancelOrder(int orderNo) throws Exception {
        orderingApiDriver.cancelOrder(orderNo).execute();
    }

    @Then("their order list should contain")
    public void thenTheirOrderListShouldContain(List<OrderByNumberData> expectedOrders) throws Exception {
        for (final OrderByNumberData expectedOrder : expectedOrders) {
            var order = orderingApiDriver.getOrder(expectedOrder.getOrderNo()).execute();
            assertEquals(expectedOrder.getStatus(), order.getStatus(), "Unexpected status for order %s.".formatted(expectedOrder.getOrderNo()));
        }
    }
}
