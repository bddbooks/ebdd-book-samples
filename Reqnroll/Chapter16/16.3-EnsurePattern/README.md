# Pattern Differences: 16.3-EnsurePattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/StepDefinitions/CustomerCollectionStepDefinitions.cs](#wimpspecsstepdefinitionscustomercollectionstepdefinitionscs)
- 📝 Modified [WIMP.Specs/Support/OrderingContext.cs](#wimpspecssupportorderingcontextcs)

## Detailed Changes

### WIMP.Specs/StepDefinitions/CustomerCollectionStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/CustomerCollectionStepDefinitions.cs#L39)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/CustomerCollectionStepDefinitions.cs#L42)</sub>

```diff
@@ -39,14 +39,7 @@ public class CustomerCollectionStepDefinitions(
     [When("they choose to collect their order")]
     public async Task WhenTheyChooseToCollectTheirOrder()
     {
-        if (orderingContext.PlacedOrderNo == null)
-        {
-            var orderRequest = new PlaceOrderRequestObjectMother().Build();
-            var placedOrder = await orderingApiDriver
-                .PlaceOrder(orderRequest)
-                .Execute();
-            orderingContext.PlacedOrderNo = placedOrder.OrderNo;
-        }
+        await orderingContext.EnsureOrderPlaced();
 
         orderCollectionDetails = await orderingApiDriver
             .SetForCollection(orderingContext.PlacedOrderNo ?? throw new InvalidOperationException("No order placed."))
```

### WIMP.Specs/Support/OrderingContext.cs

[View file](After/WIMP.Specs/Support/OrderingContext.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/OrderingContext.cs#L1-L19)</sub>

```diff
@@ -1,6 +1,20 @@
+using WIMP.Specs.Drivers;
+
 namespace WIMP.Specs.Support;
 
-public class OrderingContext
+public class OrderingContext(OrderingApiDriver orderingApiDriver)
 {
     public int? PlacedOrderNo { get; set; }
+
+    public async Task EnsureOrderPlaced()
+    {
+        if (PlacedOrderNo == null)
+        {
+            var orderRequest = new PlaceOrderRequestObjectMother().Build();
+            var placedOrder = await orderingApiDriver
+                .PlaceOrder(orderRequest)
+                .Execute();
+            PlacedOrderNo = placedOrder.OrderNo;
+        }
+    }
 }
```
