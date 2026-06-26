# Pattern Differences: 14.2-TheDefaultInstancePattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpspecsstepdefinitionsorderingstepdefinitionscs)
- 📝 Modified [WIMP.Specs/Support/DomainDefaults.cs](#wimpspecssupportdomaindefaultscs)

## Detailed Changes

### WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L13)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L16-L17)</sub>

```diff
@@ -13,7 +13,8 @@ public class OrderingStepDefinitions(OrderingContext orderingContext, OrderServi
     public void GivenTheCustomerHasPlacedAnOrderContaining(string pizzaName)
     {
         var order = new Order();
-        var pizzaItem = new PizzaItem(pizzaName, DomainDefaults.PizzaSize, DomainDefaults.PizzaStyle);
+        var pizzaItem = DomainDefaults.PizzaItemDefaultInstance();
+        pizzaItem.Name = pizzaName;
         order.AddItem(pizzaItem);
         order.DeliveryAddress = DomainDefaults.DeliveryAddress;
 
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L32-L33)</sub>

```diff
@@ -28,7 +29,8 @@ public class OrderingStepDefinitions(OrderingContext orderingContext, OrderServi
         var order = new Order();
         for (int i = 0; i < count; i++)
         {
-            var pizzaItem = new PizzaItem(DomainDefaults.PizzaName, pizzaSize, DomainDefaults.PizzaStyle);
+            var pizzaItem = DomainDefaults.PizzaItemDefaultInstance();
+            pizzaItem.Size = pizzaSize;
             order.AddItem(pizzaItem);
         }
         order.DeliveryAddress = DomainDefaults.DeliveryAddress;
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L47)</sub>

```diff
@@ -42,7 +44,7 @@ public class OrderingStepDefinitions(OrderingContext orderingContext, OrderServi
     public void GivenTheCustomerHasPlacedAnOrder()
     {
         var order = new Order();
-        var pizzaItem = new PizzaItem(DomainDefaults.PizzaName, DomainDefaults.PizzaSize, DomainDefaults.PizzaStyle);
+        var pizzaItem = DomainDefaults.PizzaItemDefaultInstance();
         order.AddItem(pizzaItem);
         order.DeliveryAddress = DomainDefaults.DeliveryAddress;
 
```

### WIMP.Specs/Support/DomainDefaults.cs

[View file](After/WIMP.Specs/Support/DomainDefaults.cs#L8)

<sub>[Jump to change](After/WIMP.Specs/Support/DomainDefaults.cs#L11-L15)</sub>

```diff
@@ -8,6 +8,11 @@ public static class DomainDefaults
     public const string PizzaSize = "Medium";
     public const PizzaStyle PizzaStyle = App.Models.PizzaStyle.Regular;
 
+    public static PizzaItem PizzaItemDefaultInstance()
+    {
+        return new PizzaItem(PizzaName, PizzaSize, PizzaStyle);
+    }
+
     public const string CustomerName = "Rebecca";
     public const string CustomerEmail = "becca@galaxy.uni";
     public const string DeliveryAddress = "2-4 Waterloo Pl, Edinburgh EH1 3EG";
```
