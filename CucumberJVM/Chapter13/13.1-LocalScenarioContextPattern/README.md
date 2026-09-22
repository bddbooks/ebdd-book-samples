# Pattern Differences: 13.1-LocalScenarioContextPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Ordering.feature](#srctestresourcescomwimpappspecsorderingfeature)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L11)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L14-L24)</sub>

```diff
@@ -11,14 +11,17 @@ import io.cucumber.java.en.When;
 import static org.junit.jupiter.api.Assertions.assertTrue;
 
 public class OrderingStepDefinitions {
+    private int placedOrderNo;
+
     @Given("the customer {string} has placed the order #{int}")
     public void theCustomerHasPlacedTheOrder(String customerName, int orderNo) {
         OrderService.placeOrder(customerName, orderNo, "Margherita");
+        placedOrderNo = orderNo;  // Store in scenario context
     }
 
-    @When("the customer {string} cancels the order #{int}")
-    public void theCustomerCancelsTheOrder(String customerName, int orderNo) {
-        OrderService.cancelOrder(customerName, orderNo);
+    @When("the customer {string} cancels the placed order")
+    public void theCustomerCancelsThePlacedOrder(String customerName) {
+        OrderService.cancelOrder(customerName, placedOrderNo);  // Use stored order number
     }
 
     @Then("the customer {string} should receive a notification about the cancellation")
```

### src/test/resources/com/wimp/app/specs/Ordering.feature

[View file](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L5)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L8)</sub>

```diff
@@ -5,5 +5,5 @@ Rule: A customer should receive a notification when their order is cancelled
 Scenario: The customer is notified about an order cancellation
   Given the customer "Rebecca" is authenticated
   And the customer "Rebecca" has placed the order #12342
-  When the customer "Rebecca" cancels the order #12342
+  When the customer "Rebecca" cancels the placed order
   Then the customer "Rebecca" should receive a notification about the cancellation
```
