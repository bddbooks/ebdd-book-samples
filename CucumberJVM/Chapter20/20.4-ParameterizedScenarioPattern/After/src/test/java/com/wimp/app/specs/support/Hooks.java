package com.wimp.app.specs.support;

import com.wimp.app.specs.drivers.DatabaseDriver;
import com.wimp.app.specs.drivers.PaymentGatewayScenarioContextProxy;
import com.wimp.app.specs.drivers.PaymentGatewaySimulator;
import io.cucumber.java.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {
    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    private final PaymentGatewaySimulator paymentGatewaySimulator;
    private final PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy;
    private final DatabaseDriver databaseDriver;
    private final TestConfigurationProvider testConfigurationProvider;

    public Hooks(PaymentGatewaySimulator paymentGatewaySimulator, PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy, DatabaseDriver databaseDriver, TestConfigurationProvider testConfigurationProvider) {
        this.paymentGatewaySimulator = paymentGatewaySimulator;
        this.paymentGatewayScenarioContextProxy = paymentGatewayScenarioContextProxy;
        this.databaseDriver = databaseDriver;
        this.testConfigurationProvider = testConfigurationProvider;
    }

    @Before(order = 0)
    public void resetDatabase() {
        log.info("Test database: use-stub={}", testConfigurationProvider.testDatabase().useStub());
        databaseDriver.emptyDatabase();
    }

    @Before(value = "@payment_gateway", order = 1)
    public void initializePaymentGateway() {
        var paymentGateway = paymentGatewaySimulator.start();
        paymentGatewayScenarioContextProxy.setPaymentGatewayOverride(paymentGateway);
    }
}
