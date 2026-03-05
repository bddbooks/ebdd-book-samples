# Pattern Differences: 13.1-TheLocalScenarioContextPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.LocalScenarioContextSample.Specs/Features/Ordering.feature](#wimplocalscenariocontextsamplespecsfeaturesorderingfeature)
- 📝 Modified [WIMP.LocalScenarioContextSample.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimplocalscenariocontextsamplespecsstepdefinitionsorderingstepdefinitionscs)

## Detailed Changes

### WIMP.LocalScenarioContextSample.Specs/Features/Ordering.feature

[View file](After/WIMP.LocalScenarioContextSample.Specs/Features/Ordering.feature#L5)

<sub>[Jump to change](After/WIMP.LocalScenarioContextSample.Specs/Features/Ordering.feature#L8)</sub>

```diff
@@ -5,5 +5,5 @@ Rule: A customer should receive a notification when their order is cancelled
 Scenario: The customer is notified about an order cancellation
   Given the customer Rebecca has logged in
   And the customer Rebecca has placed the order #12342
-  When the customer Rebecca cancels the order #12342
+  When the customer Rebecca cancels the placed order
   Then the customer Rebecca should receive a notification about the cancellation
```

### WIMP.LocalScenarioContextSample.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.LocalScenarioContextSample.Specs/StepDefinitions/OrderingStepDefinitions.cs#L8)

<sub>[Jump to change](After/WIMP.LocalScenarioContextSample.Specs/StepDefinitions/OrderingStepDefinitions.cs#L11-L23)</sub>

```diff
@@ -8,16 +8,19 @@ namespace WIMP.LocalScenarioContextSample.Specs.StepDefinitions;
 [Binding]
 public class OrderingStepDefinitions
 {
+    private int placedOrderNo;
+
     [Given("the customer {word} has placed the order #{int}")]
     public void GivenTheCustomerHasPlacedTheOrder(string customerName, int orderNo)
     {
         OrderService.PlaceOrder(customerName, orderNo, "Margherita");
+        placedOrderNo = orderNo;  // Store in scenario context
     }
 
-    [When("the customer {word} cancels the order #{int}")]
-    public void WhenTheCustomerCancelsTheOrder(string customerName, int orderNo)
+    [When("the customer {word} cancels the placed order")]
+    public void WhenTheCustomerCancelsThePlacedOrder(string customerName)
     {
-        OrderService.CancelOrder(customerName, orderNo);
+        OrderService.CancelOrder(customerName, placedOrderNo);  // Use stored order number
     }
 
     [Then("the customer {word} should receive a notification about the cancellation")]
```
