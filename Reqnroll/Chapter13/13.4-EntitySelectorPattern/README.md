# Pattern Differences: 13.4-EntitySelectorPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Features/OrderProcessing.feature](#wimpspecsfeaturesorderprocessingfeature)
- 📝 Modified [WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpspecsstepdefinitionsorderingstepdefinitionscs)
- 📝 Modified [WIMP.Specs/Support/CustomParameterTypes.cs](#wimpspecssupportcustomparametertypescs)

## Detailed Changes

### WIMP.Specs/Features/OrderProcessing.feature

[View file](After/WIMP.Specs/Features/OrderProcessing.feature#L8)

<sub>[Jump to change](After/WIMP.Specs/Features/OrderProcessing.feature#L11)</sub>

```diff
@@ -8,4 +8,4 @@ Scenario: Start work on orders in order of arrival
     | 13:33:47  |
     | 13:45:30  |
   When a kitchen staff member asks for an order to work on
-  Then the order placed at 13:33:47 should be taken
+  Then the earliest order received should be taken
```

### WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L33)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L36-L40)</sub>

```diff
@@ -33,13 +33,11 @@ public class OrderingStepDefinitions(OrderingContext orderingContext)
         orderingContext.TakenOrderNo = order?.OrderNo;
     }
 
-    [Then("the order placed at {word} should be taken")]
-    public void ThenTheOrderPlacedAtShouldBeTaken(string placedAt)
+    [Then("{order} should be taken")]
+    public void ThenTheOrderShouldBeTaken(int orderNo)
     {
         Assert.IsNotNull(orderingContext.TakenOrderNo);
-        var expectedTakenOrder = orderingContext.PlacedOrders
-            .First(o => o.PlacingTime == TimeSpan.Parse(placedAt));
-        Assert.AreEqual(expectedTakenOrder.OrderNo, orderingContext.TakenOrderNo);
+        Assert.AreEqual(orderNo, orderingContext.TakenOrderNo);
     }
 
     #region Reset database for every scenario execution
```

### WIMP.Specs/Support/CustomParameterTypes.cs

[View file](After/WIMP.Specs/Support/CustomParameterTypes.cs#L3)

<sub>[Jump to change](After/WIMP.Specs/Support/CustomParameterTypes.cs#L6-L15)</sub>

```diff
@@ -3,6 +3,14 @@ using Reqnroll;
 namespace WIMP.Specs.Support;
 
 [Binding]
-public class CustomParameterTypes
+public class CustomParameterTypes(OrderingContext orderingContext)
 {
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
