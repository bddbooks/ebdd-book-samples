# Pattern Differences: 16.2-ContextShortcutPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- ➕ Added [WIMP.Specs/Drivers/BackdoorApiDriver.cs](#wimpspecsdriversbackdoorapidrivercs)
- 📝 Modified [WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpspecsstepdefinitionsorderingstepdefinitionscs)

## Detailed Changes

### WIMP.Specs/Drivers/BackdoorApiDriver.cs

[View file](After/WIMP.Specs/Drivers/BackdoorApiDriver.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/BackdoorApiDriver.cs#L1-L14)</sub>

```diff
@@ -0,0 +1,14 @@
+using WIMP.App.Models;
+using WIMP.App.RestApi;
+using WIMP.Specs.Support;
+
+namespace WIMP.Specs.Drivers;
+
+public class BackdoorApiDriver(RestApiContext restApiContext)
+{
+    public TestAction<Order> PrepareOrder(string customerName, PlaceOrderRequest orderRequest, OrderStatus status) =>
+        new LambdaAction<Order>("Prepare test order", async () =>
+            await restApiContext.ProcessRequest<Order>(
+                "Prepare test order", HttpMethod.Post, $"/api/test/prepare-order?customerName={customerName}&status={status}",
+                orderRequest));
+}
```

### WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L7)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L10-L17)</sub>

```diff
@@ -7,29 +7,14 @@ using WIMP.Specs.Support;
 namespace WIMP.Specs.StepDefinitions;
 
 [Binding]
-public class OrderingStepDefinitions(OrderingContext orderingContext, AuthenticationApiDriver authApiDriver,
-    OrderingApiDriver orderingApiDriver, KitchenApiDriver kitchenApiDriver)
+public class OrderingStepDefinitions(OrderingContext orderingContext, BackdoorApiDriver backdoorApiDriver)
 {
     [Given("the customer has an order that is waiting for pickup")]
     public async Task GivenTheCustomerHasAnOrderThatIsWaitingForPickup()
     {
-        await authApiDriver
-            .Login(DomainDefaults.CustomerName, DomainDefaults.Password)
-            .Execute();
         var orderRequest = new PlaceOrderRequestObjectMother().Build();
-        var placedOrder = await orderingApiDriver.PlaceOrder(orderRequest)
-            .Execute();
-        await authApiDriver
-            .Login(DomainDefaults.KitchenStaff, DomainDefaults.Password)
-            .Execute();
-
-        Order? takenOrder = null;
-        while (takenOrder?.OrderNo != placedOrder.OrderNo)
-        {
-            takenOrder = await kitchenApiDriver.TakeNextOrder().Execute();
-        }
-
-        await kitchenApiDriver.SetReady(placedOrder.OrderNo)
+        var placedOrder = await backdoorApiDriver
+            .PrepareOrder(DomainDefaults.CustomerName, orderRequest, OrderStatus.WaitingForPickup)
             .Execute();
         orderingContext.PlacedOrderNo = placedOrder.OrderNo;
     }
```
