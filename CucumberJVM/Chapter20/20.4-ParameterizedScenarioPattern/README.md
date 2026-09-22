# Pattern Differences: 20.4-ParameterizedScenarioPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

The sample uses stub database by default. If you wish to try it with real database by setting `test.database.use-stub` to `false` from command line or `pom.xml`.

For enabling this from command line invoke:
* `mvn test "-Dtest.database.use-stub=false"`

For enabling this from `pom.xml`:
* Uncomment the line `<test.database.use-stub>` in `configuration`/`systemPropertyVariables` section of the `maven-surefire-plugin`.

Running the sample with real database requires a MySQL database to be running using Docker, therefore you need to have Docker installed and running on your machine.


## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/PricingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionspricingstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java](#srctestjavacomwimpappspecssupportcustomparametertypesjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java](#srctestjavacomwimpappspecssupporttestconfigurationproviderjava)
- 📝 Modified [src/test/resources/application-test.properties](#srctestresourcesapplicationtestproperties)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Pricing.feature](#srctestresourcescomwimpappspecspricingfeature)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/PricingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/PricingStepDefinitions.java#L19)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/PricingStepDefinitions.java#L22-L24)</sub>

```diff
@@ -19,9 +19,9 @@ public class PricingStepDefinitions {
 
     public PricingStepDefinitions(PricingService pricingService) { this.pricingService = pricingService; }
 
-    @Given("the net price of the {word} pizza is ${decimal}")
-    public void theNetPriceOfThePizzaIs(String pizzaName, BigDecimal netUnitPrice) {
-        pizzaNetPrices.put(pizzaName, new CurrencyValue(netUnitPrice, "USD"));
+    @Given("the net price of the {word} pizza is {market-currency}")
+    public void theNetPriceOfThePizzaIs(String pizzaName, CurrencyValue netUnitPrice) {
+        pizzaNetPrices.put(pizzaName, netUnitPrice);
     }
 
     @When("the price of {int} {word} pizzas are calculated")
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/PricingStepDefinitions.java#L32-L37)</sub>

```diff
@@ -29,12 +29,12 @@ public class PricingStepDefinitions {
         calculatedPrice = pricingService.calculateItemPrice(pizzaNetPrices.get(pizzaName), count);
     }
 
-    @Then("the sum net price is ${decimal}")
-    public void theSumNetPriceIs(BigDecimal expectedPrice) {
-        assertThat(calculatedPrice == null ? null : calculatedPrice.netPrice()).isEqualByComparingTo(new CurrencyValue(expectedPrice, "USD"));
+    @Then("the sum net price is {market-currency}")
+    public void theSumNetPriceIs(CurrencyValue expectedPrice) {
+        assertThat(calculatedPrice == null ? null : calculatedPrice.netPrice()).isEqualByComparingTo(expectedPrice);
     }
 
-    @Then("the sales tax is {decimal} percent")
+    @Then("the sales tax is {market-decimal} percent")
     public void theSalesTaxIsPercent(BigDecimal expectedPercent) {
         assertThat(calculatedPrice == null ? null : calculatedPrice.salesTaxPercent()).isEqualByComparingTo(expectedPercent);
     }
```

### src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java

[View file](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L3-L29)</sub>

```diff
@@ -1,11 +1,32 @@
 package com.wimp.app.specs.support;
 
+import com.wimp.app.models.CurrencyValue;
 import io.cucumber.java.ParameterType;
 
 import java.math.BigDecimal;
 
 public class CustomParameterTypes {
 
+    private final TestConfigurationProvider testConfigurationProvider;
+
+    public CustomParameterTypes(TestConfigurationProvider testConfigurationProvider) {
+        this.testConfigurationProvider = testConfigurationProvider;
+    }
+
+    @ParameterType(value = "\\[CUR\\]([0-9.]+)", name = "market-currency")
+    public CurrencyValue convertCurrency(String value) {
+        return new CurrencyValue(new BigDecimal(value), testConfigurationProvider.market().currency());
+    }
+
+    @ParameterType(value = "\\[([A-Z]+)\\]", name = "market-decimal")
+    public BigDecimal convertMarketDecimal(String configKey) {
+        //noinspection SwitchStatementWithTooFewBranches
+        return switch (configKey.toLowerCase()){
+            case "tax" -> testConfigurationProvider.market().tax();
+            default -> throw new IllegalArgumentException("Unable to get market value for key: %s".formatted(configKey));
+        };
+    }
+
     @ParameterType("\\d+(?:\\.\\d{1,2})?")
     public BigDecimal decimal(String value){
         return new BigDecimal(value);
```

### src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java

[View file](After/src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java#L16)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java#L19)</sub>

```diff
@@ -16,6 +16,7 @@ import org.springframework.stereotype.Component;
 public record TestConfigurationProvider(
     PaymentGatewayConfiguration paymentGateway,
     TestDatabaseConfiguration testDatabase,
+    AppConfigurationProvider.AppMarketConfiguration market,
     JdbcConnectionDetails database,
     AppConfigurationProvider.AppBackdoorConfiguration backdoor) {
 
```

### src/test/resources/application-test.properties

[View file](After/src/test/resources/application-test.properties#L4)

<sub>[Jump to change](After/src/test/resources/application-test.properties#L7-L11)</sub>

```diff
@@ -4,3 +4,8 @@
 
 test.payment-gateway.url=http://localhost/pgwsim
 test.payment-gateway.api-key=39845yjkhsd8ke
+
+# using alternative market settings for test execution
+app.market.currency=GBP
+app.market.tax=20.0
+
```

### src/test/resources/com/wimp/app/specs/Pricing.feature

[View file](After/src/test/resources/com/wimp/app/specs/Pricing.feature#L1)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Pricing.feature#L4-L7)</sub>

```diff
@@ -1,7 +1,7 @@
 ∩╗┐Feature: Pricing
 
 Scenario: Price calculation
-  Given the net price of the Margherita pizza is $8
+  Given the net price of the Margherita pizza is [CUR]8
   When the price of 3 Margherita pizzas are calculated
-  Then the sum net price is $24
-  And the sales tax is 6 percent
+  Then the sum net price is [CUR]24
+  And the sales tax is [TAX] percent
```
