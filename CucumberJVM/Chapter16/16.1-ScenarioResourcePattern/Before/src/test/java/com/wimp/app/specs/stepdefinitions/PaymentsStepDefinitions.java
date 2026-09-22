package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.Payment;
import com.wimp.app.specs.drivers.PaymentApiDriver;
import com.wimp.app.specs.drivers.PaymentGatewaySimulator;
import com.wimp.app.specs.support.OrderingContext;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentsStepDefinitions {
    private final OrderingContext orderingContext;
    private final PaymentApiDriver paymentApiDriver;
    private final PaymentGatewaySimulator.SimulatedPaymentGateway simulatedPaymentGateway;
    private Payment payment;

    public PaymentsStepDefinitions(OrderingContext orderingContext, PaymentApiDriver paymentApiDriver, PaymentGatewaySimulator.SimulatedPaymentGateway simulatedPaymentGateway) {
        this.orderingContext = orderingContext;
        this.paymentApiDriver = paymentApiDriver;
        this.simulatedPaymentGateway = simulatedPaymentGateway;
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
        assertNotNull(simulatedPaymentGateway.getPaymentByReference(payment.getPaymentReference()), "Payment reference is invalid");
    }
}
