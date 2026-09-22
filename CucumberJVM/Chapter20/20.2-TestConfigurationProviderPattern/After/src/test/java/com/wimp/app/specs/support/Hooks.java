package com.wimp.app.specs.support;

import com.wimp.app.specs.drivers.PaymentGatewayScenarioContextProxy;
import com.wimp.app.specs.drivers.PaymentGatewaySimulator;
import io.cucumber.java.*;

public class Hooks {
    private final PaymentGatewaySimulator paymentGatewaySimulator;
    private final PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy;

    public Hooks(PaymentGatewaySimulator paymentGatewaySimulator, PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy) {
        this.paymentGatewaySimulator = paymentGatewaySimulator;
        this.paymentGatewayScenarioContextProxy = paymentGatewayScenarioContextProxy;
    }

    @Before(value = "@payment_gateway", order = 1)
    public void initializePaymentGateway() {
        var paymentGateway = paymentGatewaySimulator.start();
        paymentGatewayScenarioContextProxy.setPaymentGatewayOverride(paymentGateway);
    }
}
