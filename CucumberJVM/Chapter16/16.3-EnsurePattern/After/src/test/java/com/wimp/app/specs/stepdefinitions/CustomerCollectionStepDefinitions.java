package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.OrderCollectionDetails;
import com.wimp.app.specs.drivers.OrderingApiDriver;
import com.wimp.app.specs.support.DataTableDiffHelper;
import com.wimp.app.specs.support.OrderingContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerCollectionStepDefinitions {
    private final OrderingContext orderingContext;
    private final OrderingApiDriver orderingApiDriver;
    private OrderCollectionDetails orderCollectionDetails;

    public CustomerCollectionStepDefinitions(OrderingContext orderingContext, OrderingApiDriver orderingApiDriver) {
        this.orderingContext = orderingContext;
        this.orderingApiDriver = orderingApiDriver;
    }

    @When("they choose to collect their order")
    public void theyChooseToCollectTheirOrder() throws Exception {
        orderingContext.ensureOrderPlaced();

        orderCollectionDetails = orderingApiDriver.setForCollection(orderingContext.getCurrentOrderNoVerified()).execute();
    }

    @Then("they should be asked to confirm contact details")
    public void theyShouldBeAskedToConfirmContactDetails() {
        assertNotNull(orderCollectionDetails, "The order was not set to customer-collection");
        assertTrue(orderCollectionDetails.isContactDetailsConfirmationRequested());
    }

    @Then("a collection receipt should be printed with")
    public void aCollectionReceiptShouldBePrintedWith(DataTable expectedCollectionDetailsDataTable) {
        assertNotNull(orderCollectionDetails, "The order was not set to customer-collection");
        var actualCollectionDetailsTable = DataTableDiffHelper.createDataTableWithHeader(List.of(orderCollectionDetails), expectedCollectionDetailsDataTable);
        expectedCollectionDetailsDataTable.diff(actualCollectionDetailsTable);
    }
}
