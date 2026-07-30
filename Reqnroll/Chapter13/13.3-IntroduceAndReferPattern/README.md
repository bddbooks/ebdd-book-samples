# Pattern Differences: 13.3-IntroduceAndReferPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Features/Ordering.feature](#wimpspecsfeaturesorderingfeature)
- 📝 Modified [WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpspecsstepdefinitionsorderingstepdefinitionscs)
- ➕ Added [WIMP.Specs/Support/CustomParameterTypes.cs](#wimpspecssupportcustomparametertypescs)
- ➕ Added [WIMP.Specs/Support/OrderingContext.cs](#wimpspecssupportorderingcontextcs)

## Detailed Changes

### WIMP.Specs/Features/Ordering.feature

[View file](After/WIMP.Specs/Features/Ordering.feature#L4)

<sub>[Jump to change](After/WIMP.Specs/Features/Ordering.feature#L7)</sub>

```diff
@@ -4,6 +4,6 @@ Rule: A customer should receive a notification when their order is cancelled
 
 Scenario: The customer is notified about an order cancellation
   Given the customer "Rebecca" is authenticated
-  And the authenticated customer has placed the order #12342
+  And the authenticated customer has placed an order
   When the authenticated customer cancels the placed order
   Then the authenticated customer should receive a notification about the cancellation
```

### WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L7)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L10-L27)</sub>

```diff
@@ -7,21 +7,24 @@ using WIMP.Specs.Support;
 namespace WIMP.Specs.StepDefinitions;
 
 [Binding]
-public class OrderingStepDefinitions(AuthenticationContext authContext)
+public class OrderingStepDefinitions(
+    OrderingContext orderingContext,
+    AuthenticationContext authContext)
 {
-    private int placedOrderNo;
-
-    [Given("the authenticated customer has placed the order #{int}")]
-    public void GivenTheAuthenticatedCustomerHasPlacedTheOrder(int orderNo)
+    [Given("the authenticated customer has placed an order")]
+    public void GivenTheAuthenticatedCustomerHasPlacedAnOrder()
     {
-        OrderService.PlaceOrder(authContext.AuthenticatedCustomerName, "Margherita", orderNo);
-        placedOrderNo = orderNo;
+        var order = OrderService.PlaceOrder(
+            authContext.AuthenticatedCustomerName, "Margherita");
+        orderingContext.CurrentOrderNo = order.OrderNo;
     }
 
-    [When("the authenticated customer cancels the placed order")]
-    public void WhenTheAuthenticatedCustomerCancelsThePlacedOrder()
+    [When("the authenticated customer cancels {order}")]
+    public void WhenTheAuthenticatedCustomerCancelsTheOrder(int orderNo)
     {
-        OrderService.CancelOrder(authContext.AuthenticatedCustomerName, placedOrderNo);
+        OrderService.CancelOrder(
+            authContext.AuthenticatedCustomerName,
+            orderNo);
     }
 
     [Then("the authenticated customer should receive a notification about the cancellation")]
```

### WIMP.Specs/Support/CustomParameterTypes.cs

[View file](After/WIMP.Specs/Support/CustomParameterTypes.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/CustomParameterTypes.cs#L1-L14)</sub>

```diff
@@ -0,0 +1,14 @@
+using Reqnroll;
+
+namespace WIMP.Specs.Support;
+
+[Binding]
+public class CustomParameterTypes(OrderingContext orderingContext)
+{
+    [StepArgumentTransformation("the order|the placed order|the new order", Name = "order")]
+    public int ConvertOrder()
+    {
+        return orderingContext.CurrentOrderNo ??
+            throw new InvalidOperationException("No current order");
+    }
+}
```

### WIMP.Specs/Support/OrderingContext.cs

[View file](After/WIMP.Specs/Support/OrderingContext.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/OrderingContext.cs#L1-L9)</sub>

```diff
@@ -0,0 +1,9 @@
+namespace WIMP.Specs.Support;
+
+/// <summary>
+/// Context class for sharing order-related data between step definition classes.
+/// </summary>
+public class OrderingContext
+{
+    public int? CurrentOrderNo { get; set; }
+}
```
