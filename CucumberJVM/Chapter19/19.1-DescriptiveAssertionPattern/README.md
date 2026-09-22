# Pattern Differences: 19.1-DescriptiveAssertionPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

In order to see the assertion messages in this sample, you need to introduce a bug to the `OrderService.TakeNextOrder()` method (for example change `OrderBy` to `OrderByDescending`).

Setting `app.simulation.bug` to `true` in `src/main/resources/application.properties` activates this bug without the need to change the `OrderService` class.


## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderProcessingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderprocessingstepdefinitionsjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderProcessingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderProcessingStepDefinitions.java#L49)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderProcessingStepDefinitions.java#L52-L59)</sub>

```diff
@@ -49,11 +49,13 @@ public class OrderProcessingStepDefinitions {
 
     @Then("{order} should be taken")
     public void theOrderShouldBeTaken(int expectedOrderNo) {
-        assertNotNull(orderingContext.getTakenOrderNo());
+        assertNotNull(orderingContext.getTakenOrderNo(), "Order was not taken");
         Order expectedOrder = orderingContext.getPlacedOrders().stream()
             .filter(order -> order.getOrderNo() == expectedOrderNo).findFirst().orElseThrow();
         Order taken = orderingContext.getPlacedOrders().stream()
             .filter(order -> order.getOrderNo() == orderingContext.getTakenOrderNo()).findFirst().orElseThrow();
-        assertEquals(expectedOrder.getOrderNo(), taken.getOrderNo());
+        assertEquals(expectedOrder.getOrderNo(), taken.getOrderNo(),
+            "Expected the order #%d (placed at %s) but got order #%d (placed at %s)"
+                .formatted(expectedOrder.getOrderNo(), expectedOrder.getPlacingTime(), taken.getOrderNo(), taken.getPlacingTime()));
     }
 }
```
