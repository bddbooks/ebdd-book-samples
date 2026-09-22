package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.ContactDetails;
import com.wimp.app.models.Order;
import com.wimp.app.services.OrderService;
import com.wimp.app.specs.support.OrderObjectMother;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerCollectionStepDefinitions {
    private final OrderService orderService;
    private Order order;
    private Exception provideContactDetailsError;

    public CustomerCollectionStepDefinitions(OrderService orderService) {
        this.orderService = orderService;
    }

    @Given("a customer has chosen to collect their order")
    public void aCustomerHasChosenToCollectTheirOrder() {
        order = new OrderObjectMother().withCustomerCollection().build();
    }

    @When("the customer provides the contact details as:")
    public void theCustomerProvidesTheContactDetailsAs(DataTable contactDetailsTable) {
        var contactDetails = contactDetailsTable.asList(ContactDetails.class).getFirst();
        provideContactDetails(contactDetails);
    }

    private void provideContactDetails(ContactDetails contactDetails) {
        try {
            provideContactDetailsError = null;
            orderService.provideContactDetails(
                Optional.of(order).orElseThrow(() -> new RuntimeException("Order not placed")),
                contactDetails);
        } catch (Exception ex) {
            provideContactDetailsError = ex;
        }
    }

    @Then("the contact details are accepted")
    public void theContactDetailsAreAccepted() {
        assertNull(provideContactDetailsError, "No error expected");
    }

    @Then("the contact details are not accepted")
    public void theContactDetailsAreNotAccepted() {
        assertNotNull(provideContactDetailsError);
    }
}
