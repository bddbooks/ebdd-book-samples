# Pattern Differences: 14.2-DefaultInstancePattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/DomainDefaults.java](#srctestjavacomwimpappspecssupportdomaindefaultsjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L31)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L34-L35)</sub>

```diff
@@ -31,7 +31,8 @@ public class OrderingStepDefinitions {
     @Given("the customer has placed an order containing a {string} pizza")
     public void theCustomerHasPlacedAnOrderContainingAPizza(String pizzaName) {
         var order = new Order();
-        var pizzaItem = new PizzaItem(pizzaName, DomainDefaults.PIZZA_SIZE, DomainDefaults.PIZZA_STYLE);
+        var pizzaItem = DomainDefaults.pizzaItemDefaultInstance();
+        pizzaItem.setName(pizzaName);
         order.addItem(pizzaItem);
         order.setDeliveryAddress(DomainDefaults.CUSTOMER_ADDRESS);
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L49-L50)</sub>

```diff
@@ -45,7 +46,8 @@ public class OrderingStepDefinitions {
         var order = new Order();
         for (int i = 0; i < count; i++)
         {
-            var pizzaItem = new PizzaItem(DomainDefaults.PIZZA_NAME, pizzaSize, DomainDefaults.PIZZA_STYLE);
+            var pizzaItem = DomainDefaults.pizzaItemDefaultInstance();
+            pizzaItem.setSize(pizzaSize);
             order.addItem(pizzaItem);
         }
         order.setDeliveryAddress(DomainDefaults.CUSTOMER_ADDRESS);
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L64)</sub>

```diff
@@ -59,7 +61,7 @@ public class OrderingStepDefinitions {
     @Given("the customer has placed an order")
     public void theCustomerHasPlacedAnOrder() {
         var order = new Order();
-        var pizzaItem = new PizzaItem(DomainDefaults.PIZZA_NAME, DomainDefaults.PIZZA_SIZE, DomainDefaults.PIZZA_STYLE);
+        var pizzaItem = DomainDefaults.pizzaItemDefaultInstance();
         order.addItem(pizzaItem);
         order.setDeliveryAddress(DomainDefaults.CUSTOMER_ADDRESS);
 
```

### src/test/java/com/wimp/app/specs/support/DomainDefaults.java

[View file](After/src/test/java/com/wimp/app/specs/support/DomainDefaults.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/DomainDefaults.java#L3)</sub>

```diff
@@ -1,5 +1,6 @@
 package com.wimp.app.specs.support;
 
+import com.wimp.app.models.PizzaItem;
 import com.wimp.app.models.PizzaStyle;
 
 public final class DomainDefaults {
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/DomainDefaults.java#L18-L21)</sub>

```diff
@@ -14,4 +15,8 @@ public final class DomainDefaults {
 
     private DomainDefaults() {
     }
+
+    public static PizzaItem pizzaItemDefaultInstance() {
+        return new PizzaItem(PIZZA_NAME, PIZZA_SIZE, PIZZA_STYLE);
+    }
 }
```
