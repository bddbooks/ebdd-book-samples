# Pattern Differences: 14.1-TheDomainDefaultsPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpspecsstepdefinitionsorderingstepdefinitionscs)
- ➕ Added [WIMP.Specs/Support/DomainDefaults.cs](#wimpspecssupportdomaindefaultscs)

## Detailed Changes

### WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L13)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L16-L18)</sub>

```diff
@@ -13,9 +13,9 @@ public class OrderingStepDefinitions(OrderingContext orderingContext, OrderServi
     public void GivenTheCustomerHasPlacedAnOrderContaining(string pizzaName)
     {
         var order = new Order();
-        order.AddItem(pizzaName, "Medium");  // Hard-coded default value
+        order.AddItem(pizzaName, DomainDefaults.PizzaSize);  // Using DomainDefaults
 
-        AuthenticationService.Login("Rebecca");
+        AuthenticationService.Login(DomainDefaults.CustomerName);
         var placedOrder = orderService.PlaceOrder(order);
         orderingContext.PlacedOrderNo = placedOrder.OrderNo;
     }
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L41)</sub>

```diff
@@ -38,7 +38,7 @@ public class OrderingStepDefinitions(OrderingContext orderingContext, OrderServi
     [Then("the customer should receive a {string} coupon via email")]
     public void ThenTheCustomerShouldReceiveACouponViaEmail(string couponCode)
     {
-        Assert.IsTrue(emailService.WasCouponSent("Rebecca", couponCode));
+        Assert.IsTrue(emailService.WasCouponSent(DomainDefaults.CustomerName, couponCode));
     }
 
     #endregion
```

### WIMP.Specs/Support/DomainDefaults.cs

[View file](After/WIMP.Specs/Support/DomainDefaults.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/DomainDefaults.cs#L1-L7)</sub>

```diff
@@ -0,0 +1,7 @@
+namespace WIMP.Specs.Support;
+
+public static class DomainDefaults
+{
+    public const string PizzaSize = "Medium";
+    public const string CustomerName = "Rebecca";
+}
```
