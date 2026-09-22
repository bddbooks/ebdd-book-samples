package com.wimp.app.specs.drivers;

import com.wimp.app.models.Payment;
import com.wimp.app.services.PaymentGateway;
import com.wimp.app.services.RealPaymentGateway;
import com.wimp.app.specs.support.Hooks;

import java.math.BigDecimal;

/**
 * A payment gateway proxy that uses the real payment gateway unless it is
 * overridden by a scenario-specific stub used for testing.
 * <p>
 * Since Cucumber Spring integration uses a single Spring Boot configuration for
 * the dependencies, these cannot be replaced only for specific scenarios. In
 * our case, only scenarios that need the simulated payment gateway are tagged
 * with '@payment_gateway'.
 * <p>
 * This class helps with the situation as it is registered as scenario-scoped
 * dependency so it can hold the scenario-specific overrides and forward any
 * calls to it. The override is set from the Hooks class. Check the
 * documentation for CucumberSpringConfiguration.StubDependencyConfiguration for
 * details.
 *
 * @see com.wimp.app.CucumberSpringConfiguration.StubDependencyConfiguration
 * @see Hooks
 */
public class PaymentGatewayScenarioContextProxy implements PaymentGateway {
    private final RealPaymentGateway realPaymentGateway;
    private PaymentGatewaySimulator.SimulatedPaymentGateway paymentGatewayOverride;

    public PaymentGatewayScenarioContextProxy(RealPaymentGateway realPaymentGateway) {
        this.realPaymentGateway = realPaymentGateway;
    }

    @Override
    public Payment authorize(String customerEmail, BigDecimal amount) {
        if (paymentGatewayOverride != null)
            return paymentGatewayOverride.authorize(customerEmail, amount);
        return realPaymentGateway.authorize(customerEmail, amount);
    }

    public void setPaymentGatewayOverride(PaymentGatewaySimulator.SimulatedPaymentGateway paymentGateway) {
        this.paymentGatewayOverride = paymentGateway;
    }

    public PaymentGatewaySimulator.SimulatedPaymentGateway getPaymentGatewayOverride() {
        return paymentGatewayOverride;
    }
}
