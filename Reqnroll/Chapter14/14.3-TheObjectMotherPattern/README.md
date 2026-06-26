# Pattern Differences: 14.3-TheObjectMotherPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpspecsstepdefinitionsorderingstepdefinitionscs)
- 📝 Modified [WIMP.Specs/Support/DomainDefaults.cs](#wimpspecssupportdomaindefaultscs)
- ➕ Added [WIMP.Specs/Support/OrderObjectMother.cs](#wimpspecssupportorderobjectmothercs)

## Detailed Changes

### WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L12)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L15-L17)</sub>

```diff
@@ -12,11 +12,9 @@ public class OrderingStepDefinitions(OrderingContext orderingContext, OrderServi
     [Given("the customer has placed an order containing a {string} pizza")]
     public void GivenTheCustomerHasPlacedAnOrderContaining(string pizzaName)
     {
-        var order = new Order();
-        var pizzaItem = DomainDefaults.PizzaItemDefaultInstance();
-        pizzaItem.Name = pizzaName;
-        order.AddItem(pizzaItem);
-        order.DeliveryAddress = DomainDefaults.DeliveryAddress;
+        var order = new OrderObjectMother()
+            .WithItem(name: pizzaName)
+            .Build();
 
         AuthenticationService.Login(DomainDefaults.CustomerName);
         var placedOrder = orderService.PlaceOrder(order);
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L27-L29)</sub>

```diff
@@ -26,14 +24,9 @@ public class OrderingStepDefinitions(OrderingContext orderingContext, OrderServi
     [When("the customer places an order for {int} pizza(s) of size {string}")]
     public void WhenTheCustomerPlacesAnOrderFor(int count, string pizzaSize)
     {
-        var order = new Order();
-        for (int i = 0; i < count; i++)
-        {
-            var pizzaItem = DomainDefaults.PizzaItemDefaultInstance();
-            pizzaItem.Size = pizzaSize;
-            order.AddItem(pizzaItem);
-        }
-        order.DeliveryAddress = DomainDefaults.DeliveryAddress;
+        var order = new OrderObjectMother()
+            .WithItems(count, size: pizzaSize)
+            .Build();
 
         AuthenticationService.Login(DomainDefaults.CustomerName);
         var placedOrder = orderService.PlaceOrder(order);
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L39)</sub>

```diff
@@ -43,10 +36,7 @@ public class OrderingStepDefinitions(OrderingContext orderingContext, OrderServi
     [Given("the customer has placed an order")]
     public void GivenTheCustomerHasPlacedAnOrder()
     {
-        var order = new Order();
-        var pizzaItem = DomainDefaults.PizzaItemDefaultInstance();
-        order.AddItem(pizzaItem);
-        order.DeliveryAddress = DomainDefaults.DeliveryAddress;
+        var order = new OrderObjectMother().Build();
 
         AuthenticationService.Login(DomainDefaults.CustomerName);
         var placedOrder = orderService.PlaceOrder(order);
```

### WIMP.Specs/Support/DomainDefaults.cs

[View file](After/WIMP.Specs/Support/DomainDefaults.cs#L8)

<sub>[Jump to change](After/WIMP.Specs/Support/DomainDefaults.cs#L11-L17)</sub>

```diff
@@ -8,9 +8,13 @@ public static class DomainDefaults
     public const string PizzaSize = "Medium";
     public const PizzaStyle PizzaStyle = App.Models.PizzaStyle.Regular;
 
-    public static PizzaItem PizzaItemDefaultInstance()
+    public static PizzaItem PizzaItemDefaultInstance(
+        string? name = null, string? size = null, PizzaStyle? style = null)
     {
-        return new PizzaItem(PizzaName, PizzaSize, PizzaStyle);
+        return new PizzaItem(
+            name ?? PizzaName,
+            size ?? PizzaSize,
+            style ?? PizzaStyle);
     }
 
     public const string CustomerName = "Rebecca";
```

### WIMP.Specs/Support/OrderObjectMother.cs

[View file](After/WIMP.Specs/Support/OrderObjectMother.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/OrderObjectMother.cs#L1-L47)</sub>

```diff
@@ -0,0 +1,47 @@
+using WIMP.App.Models;
+
+namespace WIMP.Specs.Support;
+
+public class OrderObjectMother
+{
+    private readonly Order order = new();
+
+    public OrderObjectMother()
+    {
+        // add default item
+        order.AddItem(DomainDefaults.PizzaItemDefaultInstance());
+        // add default address
+        order.DeliveryAddress = DomainDefaults.DeliveryAddress;
+    }
+
+    public Order Build()
+    {
+        return order;
+    }
+
+    public OrderObjectMother WithAdditionalItem(
+        string? name = null, string? size = null, PizzaStyle? style = null)
+    {
+        var pizzaItem = DomainDefaults.PizzaItemDefaultInstance(name, size, style);
+        order.AddItem(pizzaItem);
+        return this;
+    }
+
+    public OrderObjectMother WithItem(
+        string? name = null, string? size = null, PizzaStyle? style = null)
+    {
+        order.ClearItems(); // remove existing items
+        return WithAdditionalItem(name, size, style);
+    }
+
+    public OrderObjectMother WithItems(int quantity,
+        string? name = null, string? size = null, PizzaStyle? style = null)
+    {
+        order.ClearItems(); // remove existing items
+        for (int i = 0; i < quantity; i++)
+        {
+            WithAdditionalItem(name, size, style);
+        }
+        return this;
+    }
+}
```
