# Pattern Differences: 20.4-ParameterizedScenarioPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

The sample uses stub database by default. If you wish to try it with real database (by changing `Database.UseStub` in `testconfig.json`), you need to setup a MySQL database.

The following instructions are for setting up a MySQL database in a Docker container.

* Create and start container (first time):  
  `docker run --name wimp-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=wimp_db -p 3306:3306 -d mysql:8.4`

* Stop container:  
  `docker stop wimp-mysql`

* Start existing container again:  
  `docker start wimp-mysql`

* Restart container:  
  `docker restart wimp-mysql`

* Start from scratch (delete container + all DB data in it):
  ```
  docker stop wimp-mysql
  docker rm wimp-mysql
  docker run --name wimp-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=wimp_db -p 3306:3306 -d mysql:8.4
  ```


## Summary of Changes

- 📝 Modified [WIMP.Specs/Features/Pricing.feature](#wimpspecsfeaturespricingfeature)
- 📝 Modified [WIMP.Specs/StepDefinitions/PricingStepDefinitions.cs](#wimpspecsstepdefinitionspricingstepdefinitionscs)
- 📝 Modified [WIMP.Specs/Support/CustomParameterTypes.cs](#wimpspecssupportcustomparametertypescs)
- 📝 Modified [WIMP.Specs/Support/DiConfiguration.cs](#wimpspecssupportdiconfigurationcs)
- 📝 Modified [WIMP.Specs/Support/TestConfigurationProvider.cs](#wimpspecssupporttestconfigurationprovidercs)
- 📝 Modified [WIMP.Specs/testconfig.json](#wimpspecstestconfigjson)

## Detailed Changes

### WIMP.Specs/Features/Pricing.feature

[View file](After/WIMP.Specs/Features/Pricing.feature#L1)

<sub>[Jump to change](After/WIMP.Specs/Features/Pricing.feature#L4-L7)</sub>

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

### WIMP.Specs/StepDefinitions/PricingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/PricingStepDefinitions.cs#L11)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/PricingStepDefinitions.cs#L14-L17)</sub>

```diff
@@ -11,10 +11,10 @@ public class PricingStepDefinitions(PricingService pricingService, IReqnrollOutp
     private readonly Dictionary<string, CurrencyValue> pizzaNetPrices = new();
     private OrderItemPrice? calculatedPrice;
 
-    [Given("the net price of the {word} pizza is ${decimal}")]
-    public void GivenTheNetPriceOfThePizzaIs(string pizzaName, decimal netUnitPrice)
+    [Given("the net price of the {word} pizza is {market-currency}")]
+    public void GivenTheNetPriceOfThePizzaIs(string pizzaName, CurrencyValue netUnitPrice)
     {
-        pizzaNetPrices[pizzaName] = new CurrencyValue(netUnitPrice, "USD");
+        pizzaNetPrices[pizzaName] = netUnitPrice;
     }
 
     [When("the price of {int} {word} pizzas are calculated")]
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/PricingStepDefinitions.cs#L28-L34)</sub>

```diff
@@ -25,13 +25,13 @@ public class PricingStepDefinitions(PricingService pricingService, IReqnrollOutp
         outputHelper.WriteLine($"Calculated price: {calculatedPrice}");
     }
 
-    [Then("the sum net price is ${decimal}")]
-    public void ThenTheSumNetPriceIs(decimal expectedPrice)
+    [Then("the sum net price is {market-currency}")]
+    public void ThenTheSumNetPriceIs(CurrencyValue expectedPrice)
     {
-        Assert.AreEqual(expectedPrice, calculatedPrice?.NetPrice.Value);
+        Assert.AreEqual(expectedPrice, calculatedPrice?.NetPrice);
     }
 
-    [Then("the sales tax is {decimal} percent")]
+    [Then("the sales tax is {market-decimal} percent")]
     public void ThenTheSalesTaxIsPercent(decimal expectedSalesTaxPercent)
     {
         Assert.AreEqual(expectedSalesTaxPercent, calculatedPrice?.SalesTaxPercent);
```

### WIMP.Specs/Support/CustomParameterTypes.cs

[View file](After/WIMP.Specs/Support/CustomParameterTypes.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/CustomParameterTypes.cs#L3-L21)</sub>

```diff
@@ -1,8 +1,22 @@
 using Reqnroll;
 
+using WIMP.App.Models;
+
 namespace WIMP.Specs.Support;
 
 [Binding]
-public class CustomParameterTypes
+public class CustomParameterTypes(TestConfigurationProvider testConfigurationProvider)
 {
+    [StepArgumentTransformation(@"\[CUR\]([0-9\.]+)", Name = "market-currency")]
+    public CurrencyValue ConvertCurrency(decimal value)
+    {
+        return new CurrencyValue(value,
+            testConfigurationProvider.Market.Currency);
+    }
+
+    [StepArgumentTransformation(@"\[([A-Z]+)\]", Name = "market-decimal")]
+    public decimal GetDecimalMarketParameter(string configKey)
+    {
+        return testConfigurationProvider.GetValue<decimal>("MARKET." + configKey);
+    }
 }
```

### WIMP.Specs/Support/DiConfiguration.cs

[View file](After/WIMP.Specs/Support/DiConfiguration.cs#L3)

<sub>[Jump to change](After/WIMP.Specs/Support/DiConfiguration.cs#L6)</sub>

```diff
@@ -3,6 +3,7 @@ using Reqnroll.BoDi;
 
 using WIMP.App.Data;
 using WIMP.App.Data.Db;
+using WIMP.App.Services;
 using WIMP.Specs.Drivers;
 
 namespace WIMP.Specs.Support;
```

<sub>[Jump to change](After/WIMP.Specs/Support/DiConfiguration.cs#L17-L21)</sub>

```diff
@@ -13,6 +14,11 @@ public class DiConfiguration(TestConfigurationProvider testConfigurationProvider
     [BeforeScenario(Order = -1)]
     public void SetupDependencies(IObjectContainer scenarioContainer)
     {
+        scenarioContainer.RegisterInstanceAs(
+            MarketService.Create(
+                testConfigurationProvider.Market.Currency,
+                testConfigurationProvider.Market.Tax));
+
         if (testConfigurationProvider.Database.UseStub)
         {
             Console.WriteLine("Using stub database");
```

### WIMP.Specs/Support/TestConfigurationProvider.cs

[View file](After/WIMP.Specs/Support/TestConfigurationProvider.cs#L18)

<sub>[Jump to change](After/WIMP.Specs/Support/TestConfigurationProvider.cs#L21-L26)</sub>

```diff
@@ -18,6 +18,12 @@ public class TestConfigurationProvider
         public string ApiKey { get; set; } = null!;
     }
 
+    public class MarketConfiguration
+    {
+        public decimal Tax { get; set; } = 6.0m;
+        public string Currency { get; set; } = "USD";
+    }
+
 
     private readonly IConfigurationRoot configurationRoot =
         new ConfigurationBuilder()
```

<sub>[Jump to change](After/WIMP.Specs/Support/TestConfigurationProvider.cs#L64-L74)</sub>

```diff
@@ -55,6 +61,17 @@ public class TestConfigurationProvider
         }
     }
 
+    public MarketConfiguration Market
+    {
+        get
+        {
+            var marketConfig = configurationRoot
+                .GetRequiredSection("Market")
+                .Get<MarketConfiguration>()!;
+            return marketConfig;
+        }
+    }
+
     public T? GetValue<T>(string configKey)
     {
         return configurationRoot.GetRequiredSection(NormalizeConfigKey(configKey)).Get<T>();
```

### WIMP.Specs/testconfig.json

[View file](After/WIMP.Specs/testconfig.json#L6)

<sub>[Jump to change](After/WIMP.Specs/testconfig.json#L9-L12)</sub>

```diff
@@ -6,5 +6,9 @@
   "PaymentGateway": {
     "Url": "http://localhost/pgwsim",
     "ApiKey": "39845yjkhsd8ke"
+  },
+  "Market": {
+    "Currency": "USD",
+    "Tax": 6.0
   }
 }
```
