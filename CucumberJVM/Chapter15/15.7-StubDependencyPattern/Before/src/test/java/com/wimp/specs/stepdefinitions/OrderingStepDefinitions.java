package com.wimp.specs.stepdefinitions;

import com.wimp.app.models.Notification;
import com.wimp.app.restapi.PlaceOrderRequest;
import com.wimp.specs.drivers.NotificationsApiDriver;
import com.wimp.specs.drivers.OrderingApiDriver;
import com.wimp.specs.support.DomainDefaults;
import com.wimp.specs.support.PlaceOrderRequestObjectMother;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderingStepDefinitions {
    private final OrderingApiDriver orderingApiDriver;
    private final NotificationsApiDriver notificationsApiDriver;

    @Autowired
    public OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, NotificationsApiDriver notificationsApiDriver) {
        this.orderingApiDriver = orderingApiDriver;
        this.notificationsApiDriver = notificationsApiDriver;
    }

    @Given("they have placed an order")
    public void theyHavePlacedAnOrder(DataTable dataTable) throws Exception {
        List<Map<String, String>> rows = dataTable.asMaps();
        LocalTime expectedDeliveryTime = LocalTime.parse(rows.getFirst().get("expected delivery time"));

        // With the real time service we cannot fast-forward time, so cannot use the specified
        // expectedDeliveryTime. Instead, we force the expected delivery time being in 0.5 seconds,
        // and we wait in the whenTheDeliveryHasNotBeenMadeBy method for the background timer loop
        // to process the subscription.
        PlaceOrderRequest placeOrderRequest = new PlaceOrderRequestObjectMother()
            .withExpectedDeliveryTime(LocalDateTime.now().plusNanos(500_000_000))
            .build();

        orderingApiDriver.placeOrder(placeOrderRequest).execute();
    }

    @When("^the delivery has not been made by (\\d{2}:\\d{2})$")
    public void whenTheDeliveryHasNotBeenMadeBy(String time) throws InterruptedException {
        // Workaround: see notes above!
        Thread.sleep(2_000);
    }

    @Then("the customer should receive a notification about the delay")
    public void theCustomerShouldReceiveANotificationAboutTheDelay() throws Exception {
        List<Notification> notifications = notificationsApiDriver.getNotifications(DomainDefaults.CUSTOMER_NAME).execute();

        assertTrue(
            notifications.stream().anyMatch(n -> n.message().toLowerCase().contains("delayed")),
            "Expected a delay notification but none was found.");
    }
}
