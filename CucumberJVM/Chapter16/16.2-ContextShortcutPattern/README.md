# Pattern Differences: 16.2-ContextShortcutPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- ➕ Added [src/test/java/com/wimp/app/specs/drivers/BackdoorApiDriver.java](#srctestjavacomwimpappspecsdriversbackdoorapidriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/drivers/BackdoorApiDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/BackdoorApiDriver.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/BackdoorApiDriver.java#L1-L37)</sub>

```diff
@@ -0,0 +1,37 @@
+package com.wimp.app.specs.drivers;
+
+import com.wimp.app.models.Order;
+import com.wimp.app.models.OrderStatus;
+import com.wimp.app.restapi.PlaceOrderRequest;
+import com.wimp.app.specs.support.LambdaAction;
+import com.wimp.app.specs.support.TestAction;
+import org.springframework.context.annotation.Profile;
+import org.springframework.stereotype.Component;
+import org.springframework.web.bind.annotation.RequestBody;
+import org.springframework.web.bind.annotation.RequestParam;
+import org.springframework.web.service.annotation.HttpExchange;
+import org.springframework.web.service.annotation.PostExchange;
+
+@Component
+@Profile("backdoor-api")
+public class BackdoorApiDriver {
+    private final BackdoorApiClient backdoorApiClient;
+
+    @HttpExchange("/api/test")
+    public interface BackdoorApiClient {
+        @PostExchange("/prepare-order")
+        Order prepareOrder(@RequestParam String customerName, @RequestParam OrderStatus status,
+                           @RequestBody PlaceOrderRequest request);
+
+    }
+
+    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
+    public BackdoorApiDriver(BackdoorApiClient backdoorApiClient) {
+        this.backdoorApiClient = backdoorApiClient;
+    }
+
+    public TestAction<Order> prepareOrder(String customerName, PlaceOrderRequest orderRequest, OrderStatus status) {
+        return new LambdaAction<>("Prepare test order", "%s/%s/%s".formatted(customerName, orderRequest, status), () ->
+            backdoorApiClient.prepareOrder(customerName, status, orderRequest));
+    }
+}
```

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L3)</sub>

```diff
@@ -1,6 +1,6 @@
 package com.wimp.app.specs.stepdefinitions;
 
-import com.wimp.app.models.Order;
+import com.wimp.app.models.OrderStatus;
 import com.wimp.app.specs.drivers.*;
 import com.wimp.app.specs.support.DomainDefaults;
 import com.wimp.app.specs.support.OrderingContext;
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L12-L23)</sub>

```diff
@@ -9,33 +9,19 @@ import io.cucumber.java.en.*;
 
 public class OrderingStepDefinitions {
     private final OrderingContext orderingContext;
-    private final AuthenticationApiDriver authenticationApiDriver;
-    private final OrderingApiDriver orderingApiDriver;
-    private final KitchenApiDriver kitchenApiDriver;
+    private final BackdoorApiDriver backdoorApiDriver;
 
-    public OrderingStepDefinitions(OrderingContext orderingContext, AuthenticationApiDriver authenticationApiDriver, OrderingApiDriver orderingApiDriver, KitchenApiDriver kitchenApiDriver) {
+    public OrderingStepDefinitions(OrderingContext orderingContext, BackdoorApiDriver backdoorApiDriver) {
         this.orderingContext = orderingContext;
-        this.authenticationApiDriver = authenticationApiDriver;
-        this.orderingApiDriver = orderingApiDriver;
-        this.kitchenApiDriver = kitchenApiDriver;
+        this.backdoorApiDriver = backdoorApiDriver;
     }
 
     @Given("the customer has an order that is waiting for pickup")
     public void theCustomerHasAnOrderThatIsWaitingForPickup() throws Exception {
-        authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.PASSWORD)
-            .execute();
         var orderRequest = new PlaceOrderRequestObjectMother().build();
-        var placedOrder = orderingApiDriver.placeOrder(orderRequest).execute();
-         authenticationApiDriver.login(DomainDefaults.KITCHEN_STAFF, DomainDefaults.PASSWORD)
+        var placedOrder = backdoorApiDriver
+            .prepareOrder(DomainDefaults.CUSTOMER_NAME, orderRequest, OrderStatus.WAITING_FOR_PICKUP)
             .execute();
-
-        Order takenOrder = null;
-        while (takenOrder == null || takenOrder.getOrderNo() != placedOrder.getOrderNo())
-        {
-            takenOrder = kitchenApiDriver.takeNextOrder().execute();
-        }
-
-        kitchenApiDriver.setReady(placedOrder.getOrderNo()).execute();
         orderingContext.setCurrentOrderNo(placedOrder.getOrderNo());
     }
 }
```
