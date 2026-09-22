# Pattern Differences: 14.4-TerminologyPromotionPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java](#srctestjavacomwimpappspecssupportcustomparametertypesjava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Ordering.feature](#srctestresourcescomwimpappspecsorderingfeature)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L22)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L25-L27)</sub>

```diff
@@ -22,9 +22,9 @@ public class OrderingStepDefinitions {
         this.orderService = orderService;
     }
 
-    @When("the customer places an order for {int} pizza(s) of size {string}")
-    public void theCustomerPlacesAnOrderForPizzasOfSize(int count, String pizzaSize) {
-        var order = new OrderObjectMother().withItems(count, PizzaSize.valueOf(pizzaSize.toUpperCase())).build();
+    @When("the customer places an order for {int} pizza(s) of size {pizza-size}")
+    public void theCustomerPlacesAnOrderForPizzasOfSize(int count, PizzaSize pizzaSize) {
+        var order = new OrderObjectMother().withItems(count, pizzaSize).build();
 
         AuthenticationService.login(DomainDefaults.CUSTOMER_NAME);
         var placedOrder = orderService.placeOrder(order);
```

### src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java

[View file](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L5)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L8-L16)</sub>

```diff
@@ -5,4 +5,13 @@ import io.cucumber.java.ParameterType;
 
 public class CustomParameterTypes {
 
+    @ParameterType(value = "(10\"|12\"|14\")", name = "pizza-size")
+    public PizzaSize convertPizzaSize(String pizzaSizeString) {
+        return switch (pizzaSizeString) {
+            case "10\"" -> PizzaSize.SMALL;
+            case "12\"" -> PizzaSize.MEDIUM;
+            case "14\"" -> PizzaSize.LARGE;
+            default -> throw new IllegalArgumentException("Invalid size: " + pizzaSizeString);
+        };
+    }
 }
```

### src/test/resources/com/wimp/app/specs/Ordering.feature

[View file](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L2)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L5)</sub>

```diff
@@ -2,5 +2,5 @@ Feature: Pizza Ordering
 
 Rule: More than 4 large pizzas cannot be delivered as a single order
   Scenario: Five large pizzas are ordered
-    When the customer places an order for 5 pizzas of size "Large"
+    When the customer places an order for 5 pizzas of size 14"
     Then the order should be rejected
```
