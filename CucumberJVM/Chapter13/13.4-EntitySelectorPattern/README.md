# Pattern Differences: 13.4-EntitySelectorPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java](#srctestjavacomwimpappspecssupportcustomparametertypesjava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/OrderProcessing.feature](#srctestresourcescomwimpappspecsorderprocessingfeature)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L42)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L45-L48)</sub>

```diff
@@ -42,14 +42,10 @@ public class OrderingStepDefinitions {
         }
     }
 
-    @Then("the order placed at {word} should be taken")
-    public void theOrderPlacedAtShouldBeTaken(String placedAt) {
+    @Then("{order} should be taken")
+    public void theOrderShouldBeTaken(int expectedTakenOrderNo) {
         assertNotNull(orderingContext.getTakenOrderNo());
-        var expectedTakenOrder = orderingContext.getPlacedOrders().stream()
-            .filter(o -> o.getPlacingTime().equals(LocalTime.parse(placedAt)))
-            .findFirst()
-            .orElseThrow(() -> new RuntimeException("No expected order"));
-        assertEquals(expectedTakenOrder.getOrderNo(), orderingContext.getTakenOrderNo());
+        assertEquals(expectedTakenOrderNo, orderingContext.getTakenOrderNo());
     }
 
     /**
```

### src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java

[View file](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L3)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L6)</sub>

```diff
@@ -3,6 +3,7 @@ package com.wimp.app.specs.support;
 import com.wimp.app.models.*;
 import io.cucumber.java.ParameterType;
 
+import java.util.Comparator;
 import java.util.Optional;
 
 public class CustomParameterTypes {
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L17-L28)</sub>

```diff
@@ -13,12 +14,18 @@ public class CustomParameterTypes {
         this.orderingContext = orderingContext;
     }
 
-    @ParameterType("the order|the placed order")
+    @ParameterType("the order|the placed order|the earliest order received")
     public int order(String value) {
         if (value.matches("the order|the placed order")){
             Optional.ofNullable(orderingContext.getCurrentOrderNo())
                 .orElseThrow(() -> new RuntimeException("No current order"));
         }
+        if (value.matches("the earliest order received")){
+            return orderingContext.getPlacedOrders().stream()
+                .min(Comparator.comparing(Order::getPlacingTime))
+                .map(Order::getOrderNo)
+                .orElseThrow(() -> new RuntimeException("No orders available"));
+        }
         throw new RuntimeException("Invalid order value: " + value);
     }
 }
```

### src/test/resources/com/wimp/app/specs/OrderProcessing.feature

[View file](After/src/test/resources/com/wimp/app/specs/OrderProcessing.feature#L8)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/OrderProcessing.feature#L11)</sub>

```diff
@@ -8,4 +8,4 @@ Scenario: Start work on orders in order of arrival
     | 13:33:47  |
     | 13:45:30  |
   When a kitchen staff member asks for an order to work on
-  Then the order placed at 13:33:47 should be taken
+  Then the earliest order received should be taken
```
