package com.wimp.specs.stepdefinitions;

import com.wimp.app.models.Notification;
import com.wimp.app.restapi.PlaceOrderRequest;
import com.wimp.specs.drivers.NotificationsApiDriver;
import com.wimp.specs.drivers.OrderingApiDriver;
import com.wimp.specs.drivers.TimeServiceDriver;
import com.wimp.specs.support.DomainDefaults;
import com.wimp.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderingStepDefinitions {
    private final TimeServiceDriver timeServiceDriver;
    private final OrderingApiDriver orderingApiDriver;
    private final NotificationsApiDriver notificationsApiDriver;

    @Autowired
    public OrderingStepDefinitions(
        TimeServiceDriver timeServiceDriver,
        OrderingApiDriver orderingApiDriver,
        NotificationsApiDriver notificationsApiDriver
    ) {
        this.timeServiceDriver = timeServiceDriver;
        this.orderingApiDriver = orderingApiDriver;
        this.notificationsApiDriver = notificationsApiDriver;
    }

    @Given("they have placed an order")
    public void theyHavePlacedAnOrder(DataTable dataTable) throws Exception {
        List<Map<String, String>> rows = dataTable.asMaps();
        LocalTime expectedDeliveryTime = LocalTime.parse(rows.getFirst().get("expected delivery time"));

        // ensuring that the placing time is before the expected delivery time
        timeServiceDriver.setCurrentTime(expectedDeliveryTime.minusMinutes(5));
        // preparing a place order request with expected delivery time (this setting is only available for testing)
        PlaceOrderRequest placeOrderRequest = new PlaceOrderRequestObjectMother()
            .withExpectedDeliveryTime(timeServiceDriver.getTodayTime(expectedDeliveryTime))
            .build();

        orderingApiDriver.placeOrder(placeOrderRequest).execute();
    }

    @When("^the delivery has not been made by (\\d{2}:\\d{2})$")
    public void whenTheDeliveryHasNotBeenMadeBy(String time) {
        timeServiceDriver.setCurrentTime(LocalTime.parse(time));
    }

    @Then("the customer should receive a notification about the delay")
    public void theCustomerShouldReceiveANotificationAboutTheDelay() throws Exception {
        List<Notification> notifications = notificationsApiDriver.getNotifications(DomainDefaults.CUSTOMER_NAME).execute();

        assertTrue(
            notifications.stream().anyMatch(n -> n.message().toLowerCase().contains("delayed")),
            "Expected a delay notification but none was found.");
    }
}
