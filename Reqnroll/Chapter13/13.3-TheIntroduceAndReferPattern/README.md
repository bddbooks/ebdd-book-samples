# Pattern Differences: 13.3-TheIntroduceAndReferPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.IntroduceAndReferSample.Specs/Features/Ordering.feature](#wimpintroduceandrefersamplespecsfeaturesorderingfeature)
- 📝 Modified [WIMP.IntroduceAndReferSample.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpintroduceandrefersamplespecsstepdefinitionsorderingstepdefinitionscs)
- ➕ Added [WIMP.IntroduceAndReferSample.Specs/Support/CustomDataTypes.cs](#wimpintroduceandrefersamplespecssupportcustomdatatypescs)
- ➕ Added [WIMP.IntroduceAndReferSample.Specs/Support/OrderingContext.cs](#wimpintroduceandrefersamplespecssupportorderingcontextcs)

## Detailed Changes

### WIMP.IntroduceAndReferSample.Specs/Features/Ordering.feature

[View file](After/WIMP.IntroduceAndReferSample.Specs/Features/Ordering.feature#L4)

<sub>[Jump to change](After/WIMP.IntroduceAndReferSample.Specs/Features/Ordering.feature#L7)</sub>

```diff
@@ -4,6 +4,6 @@ Rule: A customer should receive a notification when their order is cancelled
 
 Scenario: The customer is notified about an order cancellation
   Given the customer Rebecca has logged in
-  And the logged in customer has placed the order #12342
+  And the logged in customer has placed an order
   When the logged in customer cancels the placed order
   Then the logged in customer should receive a notification about the cancellation
```

### WIMP.IntroduceAndReferSample.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.IntroduceAndReferSample.Specs/StepDefinitions/OrderingStepDefinitions.cs#L7)

<sub>[Jump to change](After/WIMP.IntroduceAndReferSample.Specs/StepDefinitions/OrderingStepDefinitions.cs#L10-L27)</sub>

```diff
@@ -7,21 +7,24 @@ using WIMP.IntroduceAndReferSample.Specs.Support;
 namespace WIMP.IntroduceAndReferSample.Specs.StepDefinitions;
 
 [Binding]
-public class OrderingStepDefinitions(AuthenticationContext authContext)
+public class OrderingStepDefinitions(
+    OrderingContext orderingContext,
+    AuthenticationContext authContext)
 {
-    private int placedOrderNo;
-
-    [Given("the logged in customer has placed the order #{int}")]
-    public void GivenTheLoggedInCustomerHasPlacedTheOrder(int orderNo)
+    [Given("the logged in customer has placed an order")]
+    public void GivenTheLoggedInCustomerHasPlacedAnOrder()
     {
-        OrderService.PlaceOrder(authContext.LoggedInCustomerName, "Margherita", orderNo);
-        placedOrderNo = orderNo;
+        var order = OrderService.PlaceOrder(
+            authContext.LoggedInCustomerName, "Margherita");
+        orderingContext.PlacedOrderNo = order.OrderNo;
     }
 
-    [When("the logged in customer cancels the placed order")]
-    public void WhenTheLoggedInCustomerCancelsThePlacedOrder()
+    [When("the logged in customer cancels {order}")]
+    public void WhenTheLoggedInCustomerCancelsTheOrder(int orderNo)
     {
-        OrderService.CancelOrder(authContext.LoggedInCustomerName, placedOrderNo);
+        OrderService.CancelOrder(
+            authContext.LoggedInCustomerName,
+            orderNo);
     }
 
     [Then("the logged in customer should receive a notification about the cancellation")]
```

### WIMP.IntroduceAndReferSample.Specs/Support/CustomDataTypes.cs

[View file](After/WIMP.IntroduceAndReferSample.Specs/Support/CustomDataTypes.cs#L1)

<sub>[Jump to change](After/WIMP.IntroduceAndReferSample.Specs/Support/CustomDataTypes.cs#L1-L24)</sub>

```diff
@@ -0,0 +1,24 @@
+using Reqnroll;
+
+using WIMP.IntroduceAndReferSample.App.Services;
+
+namespace WIMP.IntroduceAndReferSample.Specs.Support;
+
+[Binding]
+public class CustomDataTypes(OrderingContext orderingContext)
+{
+    [StepArgumentTransformation("the order|the placed order", Name = "order")]
+    public int ConvertOrder()
+    {
+        return orderingContext.PlacedOrderNo ??
+            throw new InvalidOperationException("Order not chosen");
+    }
+
+    [StepArgumentTransformation(@"the order #(\d+)", Name = "order")]
+    public int ConvertOrderNumber(int orderNo)
+    {
+        var order = OrderService.GetOrder(orderNo);
+        return order?.OrderNo ??
+            throw new InvalidOperationException("Order not found");
+    }
+}
```

### WIMP.IntroduceAndReferSample.Specs/Support/OrderingContext.cs

[View file](After/WIMP.IntroduceAndReferSample.Specs/Support/OrderingContext.cs#L1)

<sub>[Jump to change](After/WIMP.IntroduceAndReferSample.Specs/Support/OrderingContext.cs#L1-L9)</sub>

```diff
@@ -0,0 +1,9 @@
+namespace WIMP.IntroduceAndReferSample.Specs.Support;
+
+/// <summary>
+/// Context class for sharing order-related data between step definition classes.
+/// </summary>
+public class OrderingContext
+{
+    public int? PlacedOrderNo { get; set; }
+}
```
