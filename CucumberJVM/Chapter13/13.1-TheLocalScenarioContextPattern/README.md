# Pattern Differences: 13.1-TheLocalScenarioContextPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/localscenariocontext/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimplocalscenariocontextspecsstepdefinitionsorderingstepdefinitionsjava)
- 📝 Modified [src/test/resources/features/Ordering.feature](#srctestresourcesfeaturesorderingfeature)

## Detailed Changes

### src/test/java/com/wimp/localscenariocontext/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/localscenariocontext/specs/stepdefinitions/OrderingStepDefinitions.java#L11)

<sub>[Jump to change](After/src/test/java/com/wimp/localscenariocontext/specs/stepdefinitions/OrderingStepDefinitions.java#L14-L24)</sub>

```diff
@@ -11,14 +11,17 @@ import io.cucumber.java.en.When;
 import static org.junit.jupiter.api.Assertions.assertTrue;
 
 public class OrderingStepDefinitions {
+    private int placedOrderNo;
+
     @Given("the customer {word} has placed the order #{int}")
     public void theCustomerHasPlacedTheOrder(String customerName, int orderNo) {
         OrderService.placeOrder(customerName, orderNo, "Margherita");
+        placedOrderNo = orderNo;  // Store in scenario context
     }
 
-    @When("the customer {word} cancels the order #{int}")
-    public void theCustomerCancelsTheOrder(String customerName, int orderNo) {
-        OrderService.cancelOrder(customerName, orderNo);
+    @When("the customer {word} cancels the placed order")
+    public void theCustomerCancelsThePlacedOrder(String customerName) {
+        OrderService.cancelOrder(customerName, placedOrderNo);  // Use stored order number
     }
 
     @Then("the customer {word} should receive a notification about the cancellation")
```

### src/test/resources/features/Ordering.feature

[View file](After/src/test/resources/features/Ordering.feature#L5)

<sub>[Jump to change](After/src/test/resources/features/Ordering.feature#L8)</sub>

```diff
@@ -5,5 +5,5 @@ Feature: Order Cancellation
     Scenario: The customer is notified about an order cancellation
       Given the customer Rebecca has logged in
       And the customer Rebecca has placed the order #12342
-      When the customer Rebecca cancels the order #12342
+      When the customer Rebecca cancels the placed order
       Then the customer Rebecca should receive a notification about the cancellation
```
