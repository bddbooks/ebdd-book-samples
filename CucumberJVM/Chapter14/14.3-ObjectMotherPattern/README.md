# Pattern Differences: 14.3-ObjectMotherPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/DomainDefaults.java](#srctestjavacomwimpappspecssupportdomaindefaultsjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/OrderObjectMother.java](#srctestjavacomwimpappspecssupportorderobjectmotherjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L9)</sub>

```diff
@@ -1,13 +1,12 @@
 package com.wimp.app.specs.stepdefinitions;
 
-import com.wimp.app.models.Order;
 import com.wimp.app.models.OrderStatus;
-import com.wimp.app.models.PizzaItem;
 import com.wimp.app.services.AuthenticationService;
 import com.wimp.app.services.EmailService;
 import com.wimp.app.services.OrderService;
 import com.wimp.app.services.PromotionService;
 import com.wimp.app.specs.support.DomainDefaults;
+import com.wimp.app.specs.support.OrderObjectMother;
 import com.wimp.app.specs.support.OrderingContext;
 import io.cucumber.java.en.*;
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L32)</sub>

```diff
@@ -30,11 +29,7 @@ public class OrderingStepDefinitions {
 
     @Given("the customer has placed an order containing a {string} pizza")
     public void theCustomerHasPlacedAnOrderContainingAPizza(String pizzaName) {
-        var order = new Order();
-        var pizzaItem = DomainDefaults.pizzaItemDefaultInstance();
-        pizzaItem.setName(pizzaName);
-        order.addItem(pizzaItem);
-        order.setDeliveryAddress(DomainDefaults.CUSTOMER_ADDRESS);
+        var order = new OrderObjectMother().withItem(pizzaName).build();
 
         AuthenticationService.login(DomainDefaults.CUSTOMER_NAME);
         var placedOrder = orderService.placeOrder(order);
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L41)</sub>

```diff
@@ -43,14 +38,7 @@ public class OrderingStepDefinitions {
 
     @When("the customer places an order for {int} pizza(s) of size {string}")
     public void theCustomerPlacesAnOrderForPizzasOfSize(int count, String pizzaSize) {
-        var order = new Order();
-        for (int i = 0; i < count; i++)
-        {
-            var pizzaItem = DomainDefaults.pizzaItemDefaultInstance();
-            pizzaItem.setSize(pizzaSize);
-            order.addItem(pizzaItem);
-        }
-        order.setDeliveryAddress(DomainDefaults.CUSTOMER_ADDRESS);
+        var order = new OrderObjectMother().withItems(count, pizzaSize).build();
 
         AuthenticationService.login(DomainDefaults.CUSTOMER_NAME);
         var placedOrder = orderService.placeOrder(order);
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L51)</sub>

```diff
@@ -60,10 +48,7 @@ public class OrderingStepDefinitions {
 
     @Given("the customer has placed an order")
     public void theCustomerHasPlacedAnOrder() {
-        var order = new Order();
-        var pizzaItem = DomainDefaults.pizzaItemDefaultInstance();
-        order.addItem(pizzaItem);
-        order.setDeliveryAddress(DomainDefaults.CUSTOMER_ADDRESS);
+        var order = new OrderObjectMother().build();
 
         AuthenticationService.login(DomainDefaults.CUSTOMER_NAME);
         var placedOrder = orderService.placeOrder(order);
```

### src/test/java/com/wimp/app/specs/support/DomainDefaults.java

[View file](After/src/test/java/com/wimp/app/specs/support/DomainDefaults.java#L17)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/DomainDefaults.java#L20-L27)</sub>

```diff
@@ -17,6 +17,13 @@ public final class DomainDefaults {
     }
 
     public static PizzaItem pizzaItemDefaultInstance() {
-        return new PizzaItem(PIZZA_NAME, PIZZA_SIZE, PIZZA_STYLE);
+        return pizzaItemDefaultInstance(null, null, null);
+    }
+
+    public static PizzaItem pizzaItemDefaultInstance(String name, String size, PizzaStyle style) {
+        return new PizzaItem(
+            name != null ? name : PIZZA_NAME,
+            size != null ? size : PIZZA_SIZE,
+            style != null ? style : PIZZA_STYLE);
     }
 }
```

### src/test/java/com/wimp/app/specs/support/OrderObjectMother.java

[View file](After/src/test/java/com/wimp/app/specs/support/OrderObjectMother.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/OrderObjectMother.java#L1-L47)</sub>

```diff
@@ -0,0 +1,47 @@
+package com.wimp.app.specs.support;
+
+import com.wimp.app.models.Order;
+import com.wimp.app.models.PizzaItem;
+import com.wimp.app.models.PizzaStyle;
+
+public class OrderObjectMother {
+    private final Order order;
+
+    public OrderObjectMother() {
+        order = new Order();
+        // add default item
+        order.addItem(DomainDefaults.pizzaItemDefaultInstance());
+        order.setDeliveryAddress(DomainDefaults.CUSTOMER_ADDRESS);
+    }
+
+    public Order build() {
+        return order;
+    }
+
+    public OrderObjectMother withAdditionalItem(String name, String size, PizzaStyle style) {
+        PizzaItem pizzaItem = DomainDefaults.pizzaItemDefaultInstance(name, size, style);
+        order.addItem(pizzaItem);
+        return this;
+    }
+
+    public OrderObjectMother withItem(String name, String size, PizzaStyle style) {
+        order.clearItems(); // remove existing items
+        return withAdditionalItem(name, size, style);
+    }
+
+    public OrderObjectMother withItem(String name) {
+        return withItem(name, null, null);
+    }
+
+    public OrderObjectMother withItems(int quantity, String name, String size, PizzaStyle style) {
+        order.clearItems(); // remove existing items
+        for (int i = 0; i < quantity; i++) {
+            withAdditionalItem(name, size, style);
+        }
+        return this;
+    }
+
+    public OrderObjectMother withItems(int quantity, String size) {
+        return withItems(quantity, null, size, null);
+    }
+}
```
