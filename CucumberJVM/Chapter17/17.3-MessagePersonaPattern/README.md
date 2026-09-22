# Pattern Differences: 17.3-MessagePersonaPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java](#srctestjavacomwimpappspecssupportcustomparametertypesjava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Ordering.feature](#srctestresourcescomwimpappspecsorderingfeature)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L29)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L32)</sub>

```diff
@@ -29,7 +29,7 @@ public class OrderingStepDefinitions {
         placeOrderResult = orderingApiDriver.placeOrder(orderRequest).attemptExecute();
     }
 
-    @Then("the order should be rejected with message {string}")
+    @Then("the order should be rejected with message {user-message}")
     public void theOrderShouldBeRejectedWithMessage(String expectedMessage) {
         placeOrderResult.assertFailedWithErrorMessageContains(expectedMessage);
     }
```

### src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java

[View file](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L4-L22)</sub>

```diff
@@ -1,10 +1,25 @@
 package com.wimp.app.specs.support;
 
 import com.wimp.app.models.*;
+import com.wimp.app.services.MessageService;
+import com.wimp.app.specs.drivers.CustomerDriver;
 import io.cucumber.java.ParameterType;
 
+import java.util.Arrays;
+
 public class CustomParameterTypes {
 
+    private final CustomerDriver customerDriver;
+    private final MessageService messageService;
+
+    public CustomParameterTypes(
+        CustomerDriver customerDriver,
+        MessageService messageService
+    ) {
+        this.customerDriver = customerDriver;
+        this.messageService = messageService;
+    }
+
     @ParameterType(value = "(10\"|12\"|14\")", name = "pizza-size")
     public PizzaSize convertPizzaSize(String pizzaSizeString) {
         return switch (pizzaSizeString) {
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L32-L40)</sub>

```diff
@@ -14,4 +29,13 @@ public class CustomParameterTypes {
             default -> throw new IllegalArgumentException("Invalid size: " + pizzaSizeString);
         };
     }
+
+    @ParameterType(value = "\\[([\\w\\-]+(?:,.+)?)\\]", name = "user-message")
+    public String convertUserMessage(String messageNameSpecification) {
+        String language = customerDriver.getInterfaceLanguage();
+        String[] specParts = messageNameSpecification.split(",");
+        String messageName = specParts[0];
+        Object[] parameters = Arrays.copyOfRange(specParts, 1, specParts.length);
+        return messageService.getMessage(language, messageName, parameters);
+    }
 }
```

### src/test/resources/com/wimp/app/specs/Ordering.feature

[View file](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L3)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L6)</sub>

```diff
@@ -3,4 +3,4 @@ Feature: Ordering
 Rule: More than 4 large pizzas cannot be delivered as a single order
   Scenario: Five large pizzas are ordered
     When the customer places an order for 5 pizzas of size 14"
-    Then the order should be rejected with message "We cannot deliver 5 large pizzas in a single order, the maximum is 4"
+    Then the order should be rejected with message [cannot-deliver-too-many-large-pizzas,5]
```
