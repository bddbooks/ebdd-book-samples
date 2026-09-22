# Pattern Differences: 14.1-DomainDefaultsPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/DomainDefaults.java](#srctestjavacomwimpappspecssupportdomaindefaultsjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L5)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L8)</sub>

```diff
@@ -5,6 +5,7 @@ import com.wimp.app.services.AuthenticationService;
 import com.wimp.app.services.EmailService;
 import com.wimp.app.services.OrderService;
 import com.wimp.app.services.PromotionService;
+import com.wimp.app.specs.support.DomainDefaults;
 import com.wimp.app.specs.support.OrderingContext;
 import io.cucumber.java.en.*;
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L30-L32)</sub>

```diff
@@ -26,9 +27,9 @@ public class OrderingStepDefinitions {
     @Given("the customer has placed an order containing a {string} pizza")
     public void theCustomerHasPlacedAnOrderContainingAPizza(String pizzaName) {
         var order = new Order();
-        order.addItem(pizzaName, "Medium");  // Hard-coded default value
+        order.addItem(pizzaName, DomainDefaults.PIZZA_SIZE);  // Using DomainDefaults
 
-        AuthenticationService.login("Rebecca");
+        AuthenticationService.login(DomainDefaults.CUSTOMER_NAME);
         var placedOrder = orderService.placeOrder(order);
         orderingContext.setCurrentOrderNo(placedOrder.getOrderNo());
     }
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L49)</sub>

```diff
@@ -45,6 +46,6 @@ public class OrderingStepDefinitions {
 
     @Then("the customer should receive a {string} coupon via email")
     public void theCustomerShouldReceiveACouponViaEmail(String couponCode) {
-        assertTrue(emailService.wasCouponSent("Rebecca", couponCode));
+        assertTrue(emailService.wasCouponSent(DomainDefaults.CUSTOMER_NAME, couponCode));
     }
 }
```

### src/test/java/com/wimp/app/specs/support/DomainDefaults.java

[View file](After/src/test/java/com/wimp/app/specs/support/DomainDefaults.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/DomainDefaults.java#L1-L9)</sub>

```diff
@@ -0,0 +1,9 @@
+package com.wimp.app.specs.support;
+
+public final class DomainDefaults {
+    public static final String CUSTOMER_NAME = "Rebecca";
+    public static final String PIZZA_SIZE = "Medium";
+
+    private DomainDefaults() {
+    }
+}
```
