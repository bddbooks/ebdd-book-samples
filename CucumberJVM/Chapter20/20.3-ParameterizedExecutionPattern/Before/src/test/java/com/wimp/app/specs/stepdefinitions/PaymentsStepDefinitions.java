package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.Payment;
import com.wimp.app.specs.drivers.PaymentApiDriver;
import com.wimp.app.specs.drivers.PaymentGatewayScenarioContextProxy;
import com.wimp.app.specs.support.OrderingContext;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentsStepDefinitions {
    private final OrderingContext orderingContext;
    private final PaymentApiDriver paymentApiDriver;
    private final PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy;
    private Payment payment;

    public PaymentsStepDefinitions(OrderingContext orderingContext, PaymentApiDriver paymentApiDriver, PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy) {
        this.orderingContext = orderingContext;
        this.paymentApiDriver = paymentApiDriver;
        this.paymentGatewayScenarioContextProxy = paymentGatewayScenarioContextProxy;
    }

    @When("their payment is authorised by the payment gateway")
    public void theirPaymentIsAuthorisedByThePaymentGateway() throws Exception {
        int orderNo = orderingContext.getCurrentOrderNoVerified();
        payment = paymentApiDriver.authorizePaymentFor(orderNo).execute();
    }

    @Then("they should be shown a success message with the payment reference")
    public void theyShouldBeShownASuccessMessageWithThePaymentReference() {
        assertNotNull(payment, "No payment was authorized");
        assertTrue(payment.isSuccess(), "The payment should be successful.");
        assertFalse(payment.getPaymentReference().isEmpty(), "The response should contain a payment reference");
        assertNotNull(paymentGatewayScenarioContextProxy.getPaymentGatewayOverride(), "No simulated payment gateway available");
        assertNotNull(paymentGatewayScenarioContextProxy.getPaymentGatewayOverride().getPaymentByReference(payment.getPaymentReference()), "Payment reference is invalid");
    }
}
