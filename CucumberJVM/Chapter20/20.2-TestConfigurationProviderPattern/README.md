# Pattern Differences: 20.2-TestConfigurationProviderPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/drivers/PaymentGatewaySimulator.java](#srctestjavacomwimpappspecsdriverspaymentgatewaysimulatorjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java](#srctestjavacomwimpappspecssupporttestconfigurationproviderjava)
- 📝 Modified [src/test/resources/application-test.properties](#srctestresourcesapplicationtestproperties)

## Detailed Changes

### src/test/java/com/wimp/app/specs/drivers/PaymentGatewaySimulator.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/PaymentGatewaySimulator.java#L2)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/PaymentGatewaySimulator.java#L5)</sub>

```diff
@@ -2,6 +2,7 @@ package com.wimp.app.specs.drivers;
 
 import com.wimp.app.models.Payment;
 import com.wimp.app.services.PaymentGateway;
+import com.wimp.app.specs.support.TestConfigurationProvider;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.stereotype.Component;
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/PaymentGatewaySimulator.java#L20-L28)</sub>

```diff
@@ -16,9 +17,15 @@ import java.util.UUID;
 public class PaymentGatewaySimulator {
     private static final Logger log = LoggerFactory.getLogger(PaymentGatewaySimulator.class);
 
+    private final TestConfigurationProvider testConfigurationProvider;
+
+    public PaymentGatewaySimulator(TestConfigurationProvider testConfigurationProvider) {
+        this.testConfigurationProvider = testConfigurationProvider;
+    }
+
     public SimulatedPaymentGateway start() {
-        String url = "http://localhost/pgwsim";
-        String apiKey = "39845yjkhsd8ke";
+        String url = testConfigurationProvider.paymentGateway().url();
+        String apiKey = testConfigurationProvider.paymentGateway().apiKey();
 
         return new SimulatedPaymentGateway(url, apiKey);
     }
```

### src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java

[View file](After/src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java#L1-L25)</sub>

```diff
@@ -0,0 +1,25 @@
+package com.wimp.app.specs.support;
+
+import com.wimp.app.config.AppConfigurationProvider;
+import org.springframework.boot.context.properties.ConfigurationProperties;
+import org.springframework.boot.jdbc.autoconfigure.JdbcConnectionDetails;
+import org.springframework.stereotype.Component;
+
+/**
+ * Exposes test-only configuration settings (bound from
+ * {@code application-test.properties} entries with the "test." prefix), plus a
+ * couple of settings that are shared with the application itself (e.g. market
+ * currency/tax, database connection string) and therefore stay defined on
+ * {@link AppConfigurationProvider}.
+ */
+@Component
+public record TestConfigurationProvider(
+    PaymentGatewayConfiguration paymentGateway,
+    AppConfigurationProvider.AppMarketConfiguration market,
+    JdbcConnectionDetails database,
+    AppConfigurationProvider.AppBackdoorConfiguration backdoor) {
+
+    @ConfigurationProperties(prefix = "test.payment-gateway")
+    public record PaymentGatewayConfiguration(String url, String apiKey) {
+    }
+}
```

### src/test/resources/application-test.properties

[View file](After/src/test/resources/application-test.properties#L1)

<sub>[Jump to change](After/src/test/resources/application-test.properties#L2-L4)</sub>

```diff
@@ -1 +1,4 @@
 # Test-specific configuration settings.
+
+test.payment-gateway.url=http://localhost/pgwsim
+test.payment-gateway.api-key=39845yjkhsd8ke
```
