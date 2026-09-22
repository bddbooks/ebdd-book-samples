# Pattern Differences: 18.4-SeededDataPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

Note: Using real database for testing is enabled by default for this sample. You can disable it by setting `test.database.use-stub` to `true` from command line or pom.xml.

This sample requires a MySQL database to be running using Docker, therefore you need to have Docker installed and running on your machine.


## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/Hooks.java](#srctestjavacomwimpappspecssupporthooksjava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Ordering.feature](#srctestresourcescomwimpappspecsorderingfeature)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L1)

```diff
@@ -1,9 +1,7 @@
 package com.wimp.app.specs.stepdefinitions;
 
-import com.wimp.app.specs.drivers.MenuBackdoorDriver;
 import com.wimp.app.specs.drivers.OrderingApiDriver;
 import com.wimp.app.specs.support.AuthenticationContext;
-import com.wimp.app.specs.support.MenuItemData;
 import com.wimp.app.specs.support.OrderingContext;
 import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
 import io.cucumber.java.en.*;
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L14)</sub>

```diff
@@ -12,23 +10,15 @@ public class OrderingStepDefinitions {
     private final OrderingContext orderingContext;
     private final AuthenticationContext authenticationContext;
     private final OrderingApiDriver orderingApiDriver;
-    private final MenuBackdoorDriver menuBackdoorDriver;
 
-    public OrderingStepDefinitions(OrderingContext orderingContext, AuthenticationContext authenticationContext, OrderingApiDriver orderingApiDriver, MenuBackdoorDriver menuBackdoorDriver) {
+    public OrderingStepDefinitions(OrderingContext orderingContext, AuthenticationContext authenticationContext, OrderingApiDriver orderingApiDriver) {
         this.orderingContext = orderingContext;
         this.authenticationContext = authenticationContext;
         this.orderingApiDriver = orderingApiDriver;
-        this.menuBackdoorDriver = menuBackdoorDriver;
     }
 
     @Given("the customer has placed an order containing a {string} pizza")
     public void theCustomerHasPlacedAnOrderContainingAPizza(String pizzaName) throws Exception {
-        // ensuring that the pizza is on the menu
-        var menu = menuBackdoorDriver.getMenuItems();
-        if (menu.stream().noneMatch(mi -> mi.getName().equals(pizzaName))) {
-            menuBackdoorDriver.addMenuItem(new MenuItemData(pizzaName));
-        }
-
         authenticationContext.ensureAuthenticatedCustomer();
         var placeOrderRequest = new PlaceOrderRequestObjectMother().withItem(pizzaName).build();
         var placedOrder = orderingApiDriver.placeOrder(placeOrderRequest).execute();
```

### src/test/java/com/wimp/app/specs/support/Hooks.java

[View file](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L4-L34)</sub>

```diff
@@ -1,21 +1,36 @@
 package com.wimp.app.specs.support;
 
 import com.wimp.app.specs.drivers.DatabaseDriver;
+import com.wimp.app.specs.drivers.MenuBackdoorDriver;
 import io.cucumber.java.*;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
+import java.math.BigDecimal;
+import java.util.List;
+
 public class Hooks {
     private static final Logger log = LoggerFactory.getLogger(Hooks.class);
 
     private final DatabaseDriver databaseDriver;
+    private final MenuBackdoorDriver menuBackdoorDriver;
 
-    public Hooks(DatabaseDriver databaseDriver) {
+    public Hooks(DatabaseDriver databaseDriver, MenuBackdoorDriver menuBackdoorDriver) {
         this.databaseDriver = databaseDriver;
+        this.menuBackdoorDriver = menuBackdoorDriver;
     }
 
     @Before(order = 0)
     public void resetDatabase() {
         databaseDriver.emptyDatabase();
+        seedMenuData();
+    }
+
+    private void seedMenuData() {
+        log.info("Seeding menu data");
+        menuBackdoorDriver.setMenuItems(List.of(
+            new MenuItemData("Margherita", new BigDecimal("7.99"), 900, true),
+            new MenuItemData("Pepperoni", new BigDecimal("9.99"), 1200, false),
+            new MenuItemData("Capricciosa", new BigDecimal("8.99"), 1100, false)));
     }
 }
```

### src/test/resources/com/wimp/app/specs/Ordering.feature

[View file](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L2)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L5)</sub>

```diff
@@ -2,7 +2,7 @@
 
 Rule: A 25% discount coupon is sent for the next purchase when Margherita pizza is ordered on a "Margherita Friday"
 
-  # This scenario assumes that a pizza named "Margherita" exists, that is ensured by the step definition of 'And the customer has placed an order...'
+  # This scenario assumes that a pizza named "Margherita" exists, that is ensured by seeded data
   Scenario: A Margherita Friday coupon is sent
     Given the "Margherita Friday" promotion is active
     And the customer has placed an order containing a "Margherita" pizza
```
