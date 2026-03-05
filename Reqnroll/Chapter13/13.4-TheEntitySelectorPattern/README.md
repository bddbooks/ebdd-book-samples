# Pattern Differences: 13.4-TheEntitySelectorPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.EntitySelectorSample.Specs/Features/OrderProcessing.feature](#wimpentityselectorsamplespecsfeaturesorderprocessingfeature)
- 📝 Modified [WIMP.EntitySelectorSample.Specs/Support/CustomDataTypes.cs](#wimpentityselectorsamplespecssupportcustomdatatypescs)

## Detailed Changes

### WIMP.EntitySelectorSample.Specs/Features/OrderProcessing.feature

[View file](After/WIMP.EntitySelectorSample.Specs/Features/OrderProcessing.feature#L4)

<sub>[Jump to change](After/WIMP.EntitySelectorSample.Specs/Features/OrderProcessing.feature#L7-L11)</sub>

```diff
@@ -4,8 +4,8 @@ Rule: Kitchen staff should work on orders in the order they arrive
 
 Scenario: Start work on orders in order of arrival
   Given the following orders have been placed
-    | Order number | Placed At |
-    | 240578       | 13:33:47  |
-    | 259827       | 13:45:30  |
+    | Placed At |
+    | 13:33:47  |
+    | 13:45:30  |
   When a kitchen staff member asks for an order to work on
-  Then the order #240578 should be taken
+  Then the earliest order received should be taken
```

### WIMP.EntitySelectorSample.Specs/Support/CustomDataTypes.cs

[View file](After/WIMP.EntitySelectorSample.Specs/Support/CustomDataTypes.cs#L5)

<sub>[Jump to change](After/WIMP.EntitySelectorSample.Specs/Support/CustomDataTypes.cs#L8)</sub>

```diff
@@ -5,7 +5,7 @@ using WIMP.EntitySelectorSample.App.Services;
 namespace WIMP.EntitySelectorSample.Specs.Support;
 
 [Binding]
-public class CustomDataTypes
+public class CustomDataTypes(OrderingContext orderingContext)
 {
     [StepArgumentTransformation(@"the order #(\d+)", Name = "order")]
     public int ConvertOrderNumber(int orderNo)
```

<sub>[Jump to change](After/WIMP.EntitySelectorSample.Specs/Support/CustomDataTypes.cs#L17-L25)</sub>

```diff
@@ -14,4 +14,13 @@ public class CustomDataTypes
         return order?.OrderNo ??
                throw new InvalidOperationException("Order not found");
     }
+
+    [StepArgumentTransformation("the earliest order received", Name = "order")]
+    public int ConvertEarliestOrder()
+    {
+        return orderingContext.PlacedOrders
+            .OrderBy(o => o.PlacingTime)
+            .FirstOrDefault()?.OrderNo ??
+            throw new InvalidOperationException("No orders available");
+    }
 }
```
