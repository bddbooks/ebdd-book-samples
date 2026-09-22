# Pattern Differences: 16.1-ScenarioResourcePattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/CucumberSpringConfiguration.java](#srctestjavacomwimpappcucumberspringconfigurationjava)
- ➕ Added [src/test/java/com/wimp/app/specs/drivers/PaymentGatewayScenarioContextProxy.java](#srctestjavacomwimpappspecsdriverspaymentgatewayscenariocontextproxyjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/PaymentsStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionspaymentsstepdefinitionsjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/Hooks.java](#srctestjavacomwimpappspecssupporthooksjava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Payments.feature](#srctestresourcescomwimpappspecspaymentsfeature)

## Detailed Changes

### src/test/java/com/wimp/app/CucumberSpringConfiguration.java

[View file](After/src/test/java/com/wimp/app/CucumberSpringConfiguration.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/CucumberSpringConfiguration.java#L3-L4)</sub>

```diff
@@ -1,6 +1,7 @@
 package com.wimp.app;
 
-import com.wimp.app.specs.drivers.PaymentGatewaySimulator;
+import com.wimp.app.services.RealPaymentGateway;
+import com.wimp.app.specs.drivers.PaymentGatewayScenarioContextProxy;
 import com.wimp.app.specs.support.RestApiContext;
 
 import io.cucumber.spring.CucumberContextConfiguration;
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/CucumberSpringConfiguration.java#L118-L131)</sub>

```diff
@@ -114,11 +115,20 @@ public class CucumberSpringConfiguration {
      */
     @TestConfiguration
     public static class StubDependencyConfiguration {
+        /**
+         * Because Cucumber uses a single Application Context configuration for
+         * executing all scenarios, it is not possible to configure a stub
+         * dependency only for a single scenario or for a set of tagged
+         * scenarios. If such behavior is needed, a proxy has to be registered
+         * that can dynamically switch between the stub and the real
+         * implementation depending on the needs of the scenario. The Scenario
+         * Resource pattern is implemented this way; see the
+         * PaymentGatewayScenarioContextProxy class for details.
+         */
         @Bean
-        // marked as scenario-scoped because it stores scenario-specific data (the forced local time), see notes above.
         @ScenarioScope
-        public PaymentGatewaySimulator.SimulatedPaymentGateway simulatedPaymentGateway(PaymentGatewaySimulator paymentGatewaySimulator) {
-            return paymentGatewaySimulator.start();
+        public PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy() {
+            return new PaymentGatewayScenarioContextProxy(new RealPaymentGateway());
         }
     }
 }
```

### src/test/java/com/wimp/app/specs/drivers/PaymentGatewayScenarioContextProxy.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/PaymentGatewayScenarioContextProxy.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/PaymentGatewayScenarioContextProxy.java#L1-L50)</sub>

```diff
@@ -0,0 +1,50 @@
+package com.wimp.app.specs.drivers;
+
+import com.wimp.app.models.Payment;
+import com.wimp.app.services.PaymentGateway;
+import com.wimp.app.services.RealPaymentGateway;
+import com.wimp.app.specs.support.Hooks;
+
+import java.math.BigDecimal;
+
+/**
+ * A payment gateway proxy that uses the real payment gateway unless it is
+ * overridden by a scenario-specific stub used for testing.
+ * <p>
+ * Since Cucumber Spring integration uses a single Spring Boot configuration for
+ * the dependencies, these cannot be replaced only for specific scenarios. In
+ * our case, only scenarios that need the simulated payment gateway are tagged
+ * with '@payment_gateway'.
+ * <p>
+ * This class helps with the situation as it is registered as scenario-scoped
+ * dependency so it can hold the scenario-specific overrides and forward any
+ * calls to it. The override is set from the Hooks class. Check the
+ * documentation for CucumberSpringConfiguration.StubDependencyConfiguration for
+ * details.
+ *
+ * @see com.wimp.app.CucumberSpringConfiguration.StubDependencyConfiguration
+ * @see Hooks
+ */
+public class PaymentGatewayScenarioContextProxy implements PaymentGateway {
+    private final RealPaymentGateway realPaymentGateway;
+    private PaymentGatewaySimulator.SimulatedPaymentGateway paymentGatewayOverride;
+
+    public PaymentGatewayScenarioContextProxy(RealPaymentGateway realPaymentGateway) {
+        this.realPaymentGateway = realPaymentGateway;
+    }
+
+    @Override
+    public Payment authorize(String customerEmail, BigDecimal amount) {
+        if (paymentGatewayOverride != null)
+            return paymentGatewayOverride.authorize(customerEmail, amount);
+        return realPaymentGateway.authorize(customerEmail, amount);
+    }
+
+    public void setPaymentGatewayOverride(PaymentGatewaySimulator.SimulatedPaymentGateway paymentGateway) {
+        this.paymentGatewayOverride = paymentGateway;
+    }
+
+    public PaymentGatewaySimulator.SimulatedPaymentGateway getPaymentGatewayOverride() {
+        return paymentGatewayOverride;
+    }
+}
```

### src/test/java/com/wimp/app/specs/stepdefinitions/PaymentsStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/PaymentsStepDefinitions.java#L2)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/PaymentsStepDefinitions.java#L5)</sub>

```diff
@@ -2,7 +2,7 @@ package com.wimp.app.specs.stepdefinitions;
 
 import com.wimp.app.models.Payment;
 import com.wimp.app.specs.drivers.PaymentApiDriver;
-import com.wimp.app.specs.drivers.PaymentGatewaySimulator;
+import com.wimp.app.specs.drivers.PaymentGatewayScenarioContextProxy;
 import com.wimp.app.specs.support.OrderingContext;
 import io.cucumber.java.en.*;
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/PaymentsStepDefinitions.java#L14-L20)</sub>

```diff
@@ -11,13 +11,13 @@ import static org.junit.jupiter.api.Assertions.*;
 public class PaymentsStepDefinitions {
     private final OrderingContext orderingContext;
     private final PaymentApiDriver paymentApiDriver;
-    private final PaymentGatewaySimulator.SimulatedPaymentGateway simulatedPaymentGateway;
+    private final PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy;
     private Payment payment;
 
-    public PaymentsStepDefinitions(OrderingContext orderingContext, PaymentApiDriver paymentApiDriver, PaymentGatewaySimulator.SimulatedPaymentGateway simulatedPaymentGateway) {
+    public PaymentsStepDefinitions(OrderingContext orderingContext, PaymentApiDriver paymentApiDriver, PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy) {
         this.orderingContext = orderingContext;
         this.paymentApiDriver = paymentApiDriver;
-        this.simulatedPaymentGateway = simulatedPaymentGateway;
+        this.paymentGatewayScenarioContextProxy = paymentGatewayScenarioContextProxy;
     }
 
     @When("their payment is authorised by the payment gateway")
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/PaymentsStepDefinitions.java#L34-L35)</sub>

```diff
@@ -31,6 +31,7 @@ public class PaymentsStepDefinitions {
         assertNotNull(payment, "No payment was authorized");
         assertTrue(payment.isSuccess(), "The payment should be successful.");
         assertFalse(payment.getPaymentReference().isEmpty(), "The response should contain a payment reference");
-        assertNotNull(simulatedPaymentGateway.getPaymentByReference(payment.getPaymentReference()), "Payment reference is invalid");
+        assertNotNull(paymentGatewayScenarioContextProxy.getPaymentGatewayOverride(), "No simulated payment gateway available");
+        assertNotNull(paymentGatewayScenarioContextProxy.getPaymentGatewayOverride().getPaymentByReference(payment.getPaymentReference()), "Payment reference is invalid");
     }
 }
```

### src/test/java/com/wimp/app/specs/support/Hooks.java

[View file](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L1-L21)</sub>

```diff
@@ -0,0 +1,21 @@
+package com.wimp.app.specs.support;
+
+import com.wimp.app.specs.drivers.PaymentGatewayScenarioContextProxy;
+import com.wimp.app.specs.drivers.PaymentGatewaySimulator;
+import io.cucumber.java.*;
+
+public class Hooks {
+    private final PaymentGatewaySimulator paymentGatewaySimulator;
+    private final PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy;
+
+    public Hooks(PaymentGatewaySimulator paymentGatewaySimulator, PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy) {
+        this.paymentGatewaySimulator = paymentGatewaySimulator;
+        this.paymentGatewayScenarioContextProxy = paymentGatewayScenarioContextProxy;
+    }
+
+    @Before(value = "@payment_gateway", order = 1)
+    public void initializePaymentGateway() {
+        var paymentGateway = paymentGatewaySimulator.start();
+        paymentGatewayScenarioContextProxy.setPaymentGatewayOverride(paymentGateway);
+    }
+}
```

### src/test/resources/com/wimp/app/specs/Payments.feature

[View file](After/src/test/resources/com/wimp/app/specs/Payments.feature#L2)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Payments.feature#L5)</sub>

```diff
@@ -2,6 +2,7 @@
 
 Rule: Card payments must be authorised by payment gateway
 
+  @payment_gateway
   Scenario: Customer is shown payment reference
     Given an authenticated customer has placed an order
     When their payment is authorised by the payment gateway
```
