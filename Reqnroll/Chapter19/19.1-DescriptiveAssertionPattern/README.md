# Pattern Differences: 19.1-DescriptiveAssertionPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

In order to see the assertion messages in this sample, you need to introduce a bug to the `OrderService.TakeNextOrder()` method (for example change `OrderBy` to `OrderByDescending`).

Uncommenting the `<DefineConstants>` setting in `WIMP.App.csproj` activates this bug without the need to change the `OrderService` class.


## Summary of Changes

- 📝 Modified [WIMP.Specs/StepDefinitions/OrderProcessingStepDefinitions.cs](#wimpspecsstepdefinitionsorderprocessingstepdefinitionscs)
- ➕ Added [WIMP.Specs/Support/AwesomeAssertionsConfiguration.cs](#wimpspecssupportawesomeassertionsconfigurationcs)
- 📝 Modified [WIMP.Specs/WIMP.Specs.csproj](#wimpspecswimpspecscsproj)

## Detailed Changes

### WIMP.Specs/StepDefinitions/OrderProcessingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderProcessingStepDefinitions.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderProcessingStepDefinitions.cs#L1-L2)</sub>

```diff
@@ -1,3 +1,5 @@
+using AwesomeAssertions;
+
 using Reqnroll;
 
 using WIMP.App.Models;
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderProcessingStepDefinitions.cs#L42-L52)</sub>

```diff
@@ -37,10 +39,16 @@ public class OrderProcessingStepDefinitions(AuthenticationApiDriver authApiDrive
     [Then("the earliest order received should be taken")]
     public void ThenTheEarliestOrderReceivedShouldBeTaken()
     {
-        Assert.IsNotNull(takenOrder);
+        takenOrder.Should().NotBeNull(because: "an order should have been taken");
+        //with MsTest assertion API:
+        //  Assert.IsNotNull(takenOrder, "Order was not taken");
 
         var earliestOrder = placedOrders.OrderBy(o => o.PlacingTime).First();
 
-        Assert.AreEqual(earliestOrder.OrderNo, takenOrder.OrderNo);
+        takenOrder.Should().Be(earliestOrder,
+            OrderNumberComparer.Value, because: "the earliest order is expected");
+        //with MsTest assertion API:
+        //  Assert.AreEqual(earliestOrder.OrderNo, takenOrder.OrderNo,
+        //      $"Expected the earliest order #{earliestOrder.OrderNo} (placed at {earliestOrder.PlacingTime}) but got order #{takenOrder.OrderNo} (placed at {takenOrder.PlacingTime})");
     }
 }
```

### WIMP.Specs/Support/AwesomeAssertionsConfiguration.cs

[View file](After/WIMP.Specs/Support/AwesomeAssertionsConfiguration.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/AwesomeAssertionsConfiguration.cs#L1-L42)</sub>

```diff
@@ -0,0 +1,42 @@
+using System.Runtime.CompilerServices;
+
+using AwesomeAssertions.Formatting;
+
+using WIMP.App.Models;
+
+namespace WIMP.Specs.Support;
+
+public class CustomOrderFormatter : IValueFormatter
+{
+    public bool CanHandle(object value) => value is Order;
+
+    public void Format(object value, FormattedObjectGraph formattedGraph, FormattingContext context, FormatChild formatChild)
+    {
+        var order = (Order)value;
+        formattedGraph.AddFragment($@"order #{order.OrderNo} (placed at {order.PlacingTime:h\:mm\:ss})");
+    }
+}
+
+public class OrderNumberComparer : IEqualityComparer<Order>
+{
+    public static readonly OrderNumberComparer Value = new();
+
+    public bool Equals(Order? x, Order? y)
+    {
+        return x?.OrderNo == y?.OrderNo;
+    }
+
+    public int GetHashCode(Order obj)
+    {
+        return obj.OrderNo;
+    }
+}
+
+public static class FormatterInitializer
+{
+    [ModuleInitializer]
+    public static void Initialize()
+    {
+        Formatter.AddFormatter(new CustomOrderFormatter());
+    }
+}
```

### WIMP.Specs/WIMP.Specs.csproj

[View file](After/WIMP.Specs/WIMP.Specs.csproj#L18)

<sub>[Jump to change](After/WIMP.Specs/WIMP.Specs.csproj#L21-L22)</sub>

```diff
@@ -18,6 +18,8 @@
   </ItemGroup>
 
   <ItemGroup>
+    <PackageReference Include="AwesomeAssertions" Version="9.4.0" />
+
     <PackageReference Include="Microsoft.AspNetCore.Mvc.Testing" Version="9.0.4" />
     <PackageReference Include="Microsoft.NET.Test.Sdk" Version="18.0.1" />
     <PackageReference Include="MSTest.TestAdapter" Version="4.0.2" />
```
