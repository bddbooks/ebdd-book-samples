package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.restapi.ChangeAddressRequest;
import com.wimp.app.specs.drivers.OrderingApiDriver;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.OrderingContext;
import com.wimp.app.specs.support.TestActionResult;
import com.wimp.app.specs.support.VoidReturn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AddressChangeStepDefinitions {
    private final OrderingContext orderingContext;
    private final OrderingApiDriver orderingApiDriver;
    private TestActionResult<VoidReturn> addressChangeResult = TestActionResult.notExecuted();

    public AddressChangeStepDefinitions(OrderingContext orderingContext, OrderingApiDriver orderingApiDriver) {
        this.orderingContext = orderingContext;
        this.orderingApiDriver = orderingApiDriver;
    }

    @When("they attempt to change the delivery address")
    public void theyAttemptToChangeTheDeliveryAddress() throws Exception {
        addressChangeResult = orderingApiDriver
            .changeDeliveryAddress(orderingContext.getCurrentOrderNoVerified(),
                new ChangeAddressRequest(DomainDefaults.ALT_DELIVERY_ADDRESS))
            .attemptExecute();
    }

    @Then("the address change should be allowed")
    public void theAddressChangeShouldBeAllowed() {
        addressChangeResult.assertSucceeded();
    }
}
