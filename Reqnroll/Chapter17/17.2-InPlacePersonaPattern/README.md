# Pattern Differences: 17.2-InPlacePersonaPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Features/Ordering.feature](#wimpspecsfeaturesorderingfeature)
- 📝 Modified [WIMP.Specs/Features/Reporting.feature](#wimpspecsfeaturesreportingfeature)
- 📝 Modified [WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpspecsstepdefinitionsorderingstepdefinitionscs)
- 📝 Modified [WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs](#wimpspecsstepdefinitionsreportingstepdefinitionscs)
- 📝 Modified [WIMP.Specs/Support/CustomParameterTypes.cs](#wimpspecssupportcustomparametertypescs)
- ➕ Added [WIMP.Specs/Support/NamedOrderData.cs](#wimpspecssupportnamedorderdatacs)
- 📝 Modified [WIMP.Specs/Support/OrderingContext.cs](#wimpspecssupportorderingcontextcs)

## Detailed Changes

### WIMP.Specs/Features/Ordering.feature

[View file](After/WIMP.Specs/Features/Ordering.feature#L4)

<sub>[Jump to change](After/WIMP.Specs/Features/Ordering.feature#L7-L17)</sub>

```diff
@@ -4,14 +4,14 @@ Rule: Open orders can be cancelled
 
   Scenario: Customer cancels one of their open orders
     Given the customer has the following orders
-      | order no | status    |
-      | 1000     | Completed |
-      | 1001     | Placed    |
-      | 1002     | Placed    |
+      | order name | status    |
+      | A          | Completed |
+      | B          | Placed    |
+      | C          | Placed    |
     And they are authenticated
-    When they cancel order 1001
+    When they cancel order B
     Then their order list should contain
-      | order no | status    |
-      | 1000     | Completed |
-      | 1001     | Cancelled |
-      | 1002     | Placed    |
+      | order name | status    |
+      | A          | Completed |
+      | B          | Cancelled |
+      | C          | Placed    |
```

### WIMP.Specs/Features/Reporting.feature

[View file](After/WIMP.Specs/Features/Reporting.feature#L1)

<sub>[Jump to change](After/WIMP.Specs/Features/Reporting.feature#L3-L35)</sub>

```diff
@@ -1,17 +1,38 @@
 Feature: Reporting
 
-Rule: Weekly sales report can be generated with daily and by pizza breakdown
+Background:
+  Given the "simple" weekly sales traffic is
+    | date       | BBQ  | Pepperoni | Margherita | Truffle Bliss |
+    | 2026-03-09 | $100 | $200      | $300       | $200          |
+    | 2026-03-10 | $300 | $0        | $300       | $100          |
+    | 2026-03-11 | $200 | $200      | $200       | $100          |
+    | 2026-03-12 | $0   | $100      | $200       | $200          |
+    | 2026-03-13 | $200 | $200      | $500       | $300          |
+    | 2026-03-14 | $700 | $300      | $800       | $500          |
+    | 2026-03-15 | $600 | $200      | $400       | $600          |
+  And the "Christmas week" weekly sales traffic is
+    | date       | BBQ  | Pepperoni | Margherita | Truffle Bliss |
+    | 2025-12-22 | $100 | $200      | $300       | $200          |
+    | 2025-12-23 | $300 | $0        | $300       | $100          |
+    | 2025-12-24 | $200 | $200      | $200       | $100          |
+    | 2025-12-25 | $0   | $0        | $0         | $0            |
+    | 2025-12-26 | $200 | $200      | $500       | $300          |
+    | 2025-12-27 | $700 | $300      | $800       | $500          |
+    | 2025-12-28 | $600 | $200      | $400       | $600          |
+  And the "truffle shortage" weekly sales traffic is
+    | date       | BBQ  | Pepperoni | Margherita | Truffle Bliss |
+    | 2026-03-09 | $100 | $200      | $300       | $0            |
+    | 2026-03-10 | $300 | $0        | $300       | $0            |
+    | 2026-03-11 | $200 | $200      | $200       | $0            |
+    | 2026-03-12 | $0   | $100      | $200       | $0            |
+    | 2026-03-13 | $200 | $200      | $500       | $0            |
+    | 2026-03-14 | $700 | $300      | $800       | $0            |
+    | 2026-03-15 | $600 | $200      | $400       | $0            |
+
+Rule: Weekly report can be generated with daily and by pizza breakdown
 
   Scenario: Weekly sales report shows totals by pizza and day
-    Given a week of operations with these pizza sales:
-      | date       | BBQ  | Pepperoni | Margherita | Truffle Bliss |
-      | 2026-03-09 | $100 | $200      | $300       | $200          |
-      | 2026-03-10 | $300 | $0        | $300       | $100          |
-      | 2026-03-11 | $200 | $200      | $200       | $100          |
-      | 2026-03-12 | $0   | $100      | $200       | $200          |
-      | 2026-03-13 | $200 | $200      | $500       | $300          |
-      | 2026-03-14 | $700 | $300      | $800       | $500          |
-      | 2026-03-15 | $600 | $200      | $400       | $600          |
+    Given the "simple" weekly sales traffic
     And the restaurant owner is authenticated
     When the sales report is requested for the week beginning 2026-03-09
     Then the report should show:
```

<sub>[Jump to change](After/WIMP.Specs/Features/Reporting.feature#L57)</sub>

```diff
@@ -33,15 +54,7 @@ Rule: Weekly sales report can be generated with daily and by pizza breakdown
       """
 
   Scenario: Christmas week sales report includes the holiday closure day
-    Given a week of operations with these pizza sales:
-      | date       | BBQ  | Pepperoni | Margherita | Truffle Bliss |
-      | 2025-12-22 | $100 | $200      | $300       | $200          |
-      | 2025-12-23 | $300 | $0        | $300       | $100          |
-      | 2025-12-24 | $200 | $200      | $200       | $100          |
-      | 2025-12-25 | $0   | $0        | $0         | $0            |
-      | 2025-12-26 | $200 | $200      | $500       | $300          |
-      | 2025-12-27 | $700 | $300      | $800       | $500          |
-      | 2025-12-28 | $600 | $200      | $400       | $600          |
+    Given the "Christmas week" weekly sales traffic
     And the restaurant owner is authenticated
     When the sales report is requested for the week beginning 2025-12-22
     Then the report should show:
```

<sub>[Jump to change](After/WIMP.Specs/Features/Reporting.feature#L79)</sub>

```diff
@@ -63,15 +76,7 @@ Rule: Weekly sales report can be generated with daily and by pizza breakdown
       """
 
   Scenario: Truffle shortage sales report has no truffle sales
-    Given a week of operations with these pizza sales:
-      | date       | BBQ  | Pepperoni | Margherita | Truffle Bliss |
-      | 2026-03-09 | $100 | $200      | $300       | $0            |
-      | 2026-03-10 | $300 | $0        | $300       | $0            |
-      | 2026-03-11 | $200 | $200      | $200       | $0            |
-      | 2026-03-12 | $0   | $100      | $200       | $0            |
-      | 2026-03-13 | $200 | $200      | $500       | $0            |
-      | 2026-03-14 | $700 | $300      | $800       | $0            |
-      | 2026-03-15 | $600 | $200      | $400       | $0            |
+    Given the "truffle shortage" weekly sales traffic
     And the restaurant owner is authenticated
     When the sales report is requested for the week beginning 2026-03-09
     Then the report should show:
```

<sub>[Jump to change](After/WIMP.Specs/Features/Reporting.feature#L103)</sub>

```diff
@@ -95,16 +100,7 @@ Rule: Weekly sales report can be generated with daily and by pizza breakdown
 Rule: Ingredient usage report can be generated for a week
 
   Scenario: Weekly sales report shows ingredients usage
-    # Uses the same sales data as the scenario 'Weekly sales report shows totals by pizza and day'
-    Given a week of operations with these pizza sales:
-      | date       | BBQ  | Pepperoni | Margherita | Truffle Bliss |
-      | 2026-03-09 | $100 | $200      | $300       | $200          |
-      | 2026-03-10 | $300 | $0        | $300       | $100          |
-      | 2026-03-11 | $200 | $200      | $200       | $100          |
-      | 2026-03-12 | $0   | $100      | $200       | $200          |
-      | 2026-03-13 | $200 | $200      | $500       | $300          |
-      | 2026-03-14 | $700 | $300      | $800       | $500          |
-      | 2026-03-15 | $600 | $200      | $400       | $600          |
+    Given the "simple" weekly sales traffic
     And the restaurant owner is authenticated
     When the ingredient usage report is requested for the week beginning 2026-03-09
     Then the ingredient usage report should show:
```

### WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L1)

```diff
@@ -1,6 +1,5 @@
 using Reqnroll;
 
-using WIMP.App.Models;
 using WIMP.Specs.Drivers;
 using WIMP.Specs.Support;
 
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L10-L29)</sub>

```diff
@@ -8,25 +7,26 @@ namespace WIMP.Specs.StepDefinitions;
 
 [Binding]
 public class OrderingStepDefinitions(
+    OrderingContext orderingContext,
     OrderingApiDriver orderingApiDriver,
     BackdoorApiDriver backdoorApiDriver)
 {
     [Given("the customer has the following orders")]
     public async Task GivenTheCustomerHasTheFollowingOrders(DataTable ordersTable)
     {
-        var orders = ordersTable.CreateSet<OrderByNumberData>().ToList();
-
+        var orders = ordersTable.CreateSet<NamedOrderData>().ToList();
         foreach (var order in orders)
         {
             var orderRequest = new PlaceOrderRequestObjectMother().Build();
-            await backdoorApiDriver
+            var placedOrder = await backdoorApiDriver
                 .PrepareOrder(DomainDefaults.CustomerName, orderRequest, order.Status)
                 .Execute();
+            orderingContext.NamedOrders[order.OrderName] = placedOrder.OrderNo;
         }
     }
 
     [When("they cancel {order}")]
-    public async Task WhenTheyCancelOrder(int orderNo)
+    public async Task WhenTheyCancelTheOrder(int orderNo)
     {
         await orderingApiDriver.CancelOrder(orderNo).Execute();
     }
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L37-L47)</sub>

```diff
@@ -34,18 +34,17 @@ public class OrderingStepDefinitions(
     [Then("their order list should contain")]
     public async Task ThenTheirOrderListShouldContain(DataTable expectedOrdersTable)
     {
-        var expectedOrders = expectedOrdersTable.CreateSet<OrderByNumberData>().ToList();
+        var expectedOrders = expectedOrdersTable.CreateSet<NamedOrderData>().ToList();
         foreach (var expectedOrder in expectedOrders)
         {
-            var order = await orderingApiDriver.GetOrder(expectedOrder.OrderNo).Execute();
+            if (!orderingContext.NamedOrders.TryGetValue(expectedOrder.OrderName, out int orderNo))
+            {
+                throw new InvalidOperationException($"Order {expectedOrder.OrderName} not known");
+            }
+
+            var order = await orderingApiDriver.GetOrder(orderNo).Execute();
             Assert.AreEqual(expectedOrder.Status, order.Status,
-                $"Unexpected status for order {expectedOrder.OrderNo}.");
+                $"Unexpected status for order {expectedOrder.OrderName}.");
         }
     }
 }
-
-public class OrderByNumberData
-{
-    public int OrderNo { get; set; }
-    public OrderStatus Status { get; set; }
-}
```

### WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L13)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L16-L33)</sub>

```diff
@@ -13,12 +13,24 @@ public class ReportingStepDefinitions(
     ReportingApiDriver reportingApiDriver,
     BackdoorApiDriver backdoorApiDriver)
 {
+    private readonly Dictionary<string, DataTable> salesTrafficSamples = new();
     private SalesReport? generatedReport;
 
-    [Given("a week of operations with these pizza sales:")]
-    public async Task GivenAWeekOfOperationsWithThesePizzaSales(DataTable salesTable)
+    [Given("the {string} weekly sales traffic is")]
+    public void GivenTheWeeklySalesTrafficIs(string name, DataTable salesTraffic)
     {
-        await backdoorApiDriver.PrepareSalesTraffic(salesTable).Execute();
+        salesTrafficSamples[name] = salesTraffic;
+    }
+
+    [Given("the {string} weekly sales traffic")]
+    public async Task GivenTheWeeklySalesTraffic(string trafficSampleName)
+    {
+        if (!salesTrafficSamples.TryGetValue(trafficSampleName, out var salesTraffic))
+        {
+            throw new InvalidOperationException($"Traffic {trafficSampleName} not known");
+        }
+
+        await backdoorApiDriver.PrepareSalesTraffic(salesTraffic).Execute();
     }
 
     [When("the sales report is requested for the week beginning {DateOnly}")]
```

### WIMP.Specs/Support/CustomParameterTypes.cs

[View file](After/WIMP.Specs/Support/CustomParameterTypes.cs#L18)

<sub>[Jump to change](After/WIMP.Specs/Support/CustomParameterTypes.cs#L21-L26)</sub>

```diff
@@ -18,9 +18,11 @@ public class CustomParameterTypes(OrderingContext orderingContext)
             throw new InvalidOperationException("No current order");
     }
 
-    [StepArgumentTransformation(@"order (\d+)", Name = "order")]
-    public int ConvertOrderNumber(int orderNo)
+    [StepArgumentTransformation(@"order ([A-Z])", Name = "order")]
+    public int ConvertNamedOrderNumber(string name)
     {
-        return orderNo;
+        return orderingContext.NamedOrders.TryGetValue(name, out int orderNo)
+            ? orderNo
+            : throw new InvalidOperationException($"Order {name} not known");
     }
 }
```

### WIMP.Specs/Support/NamedOrderData.cs

[View file](After/WIMP.Specs/Support/NamedOrderData.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/NamedOrderData.cs#L1-L9)</sub>

```diff
@@ -0,0 +1,9 @@
+using WIMP.App.Models;
+
+namespace WIMP.Specs.Support;
+
+public class NamedOrderData
+{
+    public string OrderName { get; set; } = string.Empty;
+    public OrderStatus Status { get; set; }
+}
```

### WIMP.Specs/Support/OrderingContext.cs

[View file](After/WIMP.Specs/Support/OrderingContext.cs#L3)

<sub>[Jump to change](After/WIMP.Specs/Support/OrderingContext.cs#L6)</sub>

```diff
@@ -3,4 +3,5 @@ namespace WIMP.Specs.Support;
 public class OrderingContext
 {
     public int? CurrentOrderNo { get; set; }
+    public Dictionary<string, int> NamedOrders { get; } = new();
 }
```
