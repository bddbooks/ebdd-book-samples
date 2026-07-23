# Pattern Differences: 14.4-TerminologyPromotionPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Features/Ordering.feature](#wimpspecsfeaturesorderingfeature)
- 📝 Modified [WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpspecsstepdefinitionsorderingstepdefinitionscs)
- ➕ Added [WIMP.Specs/Support/CustomParameterTypes.cs](#wimpspecssupportcustomparametertypescs)

## Detailed Changes

### WIMP.Specs/Features/Ordering.feature

[View file](After/WIMP.Specs/Features/Ordering.feature#L2)

<sub>[Jump to change](After/WIMP.Specs/Features/Ordering.feature#L5)</sub>

```diff
@@ -2,5 +2,5 @@ Feature: Pizza Ordering
 
 Rule: More than 4 large pizzas cannot be delivered as a single order
   Scenario: Five large pizzas are ordered
-    When the customer places an order for 5 pizzas of size "Large"
+    When the customer places an order for 5 pizzas of size 14"
     Then the order should be rejected
```

### WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L9)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L12)</sub>

```diff
@@ -9,7 +9,7 @@ namespace WIMP.Specs.StepDefinitions;
 [Binding]
 public class OrderingStepDefinitions(OrderingContext orderingContext, OrderService orderService)
 {
-    [When("the customer places an order for {int} pizza(s) of size {string}")]
+    [When("the customer places an order for {int} pizza(s) of size {pizza-size}")]
     public void WhenTheCustomerPlacesAnOrderFor(int count, PizzaSize pizzaSize)
     {
         var order = new OrderObjectMother()
```

### WIMP.Specs/Support/CustomParameterTypes.cs

[View file](After/WIMP.Specs/Support/CustomParameterTypes.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/CustomParameterTypes.cs#L1-L19)</sub>

```diff
@@ -0,0 +1,19 @@
+using Reqnroll;
+
+using WIMP.App.Models;
+
+namespace WIMP.Specs.Support;
+
+[Binding]
+public class CustomParameterTypes
+{
+    [StepArgumentTransformation("(10\"|12\"|14\")", Name = "pizza-size")]
+    public PizzaSize ConvertPizzaSize(string pizzaSizeString) =>
+        pizzaSizeString switch
+        {
+            "10\"" => PizzaSize.Small,
+            "12\"" => PizzaSize.Medium,
+            "14\"" => PizzaSize.Large,
+            _ => throw new FormatException($"Invalid size: {pizzaSizeString}")
+        };
+}
```
