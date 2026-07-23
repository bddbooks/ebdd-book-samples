# Pattern Differences: 16.4-ApprovedOutcomePattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Features/Reporting.feature](#wimpspecsfeaturesreportingfeature)
- 📝 Modified [WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs](#wimpspecsstepdefinitionsreportingstepdefinitionscs)
- ➕ Added [WIMP.Specs/Support/ReportPrinter.cs](#wimpspecssupportreportprintercs)
- 📝 Modified [WIMP.Specs/WIMP.Specs.csproj](#wimpspecswimpspecscsproj)

## Detailed Changes

### WIMP.Specs/Features/Reporting.feature

[View file](After/WIMP.Specs/Features/Reporting.feature#L14)

<sub>[Jump to change](After/WIMP.Specs/Features/Reporting.feature#L17-L32)</sub>

```diff
@@ -14,7 +14,19 @@ Rule: Weekly report can be generated with daily and by pizza breakdown
       | 2026-03-15 | $600 | $200      | $400       |
     And the restaurant owner is logged in
     When the sales report is requested for the week beginning 2026-03-09
-    Then the report should show a total sales volume of $6000
-    And there should be $1200 Pepperoni, $2700 Margherita, and $2100 BBQ sales on the report
-    And there should be a by day breakdown on the report
-    And all values should be also shown as percentages of the total
+    Then the report should show:
+      """
+      * By Pizza
+        * BBQ: $2100 (35%)
+        * Margherita: $2700 (45%)
+        * Pepperoni: $1200 (20%)
+      * By Day
+        * 2026-03-09: $600 (10%)
+        * 2026-03-10: $600 (10%)
+        * 2026-03-11: $600 (10%)
+        * 2026-03-12: $300 (5%)
+        * 2026-03-13: $900 (15%)
+        * 2026-03-14: $1800 (30%)
+        * 2026-03-15: $1200 (20%)
+      * Total: $6000 (100%)
+      """
```

### WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L3-L9)</sub>

```diff
@@ -1,9 +1,12 @@
 using System.Globalization;
 
+using DiffPlex.Renderer;
+
 using Reqnroll;
 
 using WIMP.App.Models;
 using WIMP.Specs.Drivers;
+using WIMP.Specs.Support;
 
 namespace WIMP.Specs.StepDefinitions;
 
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L47-L54)</sub>

```diff
@@ -41,50 +44,13 @@ public class ReportingStepDefinitions(
         generatedReport = await reportingApiDriver.GenerateSalesReport(startDay).Execute();
     }
 
-    [Then("the report should show a total sales volume of ${int}")]
-    public void ThenTheReportShouldShowATotalSalesVolumeOf(decimal expectedTotalSales)
+    [Then("the report should show:")]
+    public void ThenTheReportShouldShow(string expectedReport)
     {
-        Assert.IsNotNull(generatedReport, "Report is not generated.");
-        Assert.AreEqual(expectedTotalSales, generatedReport.TotalSales.Sales, "Unexpected total sales.");
-    }
-
-    [Then("there should be ${int} Pepperoni, ${int} Margherita, and ${int} BBQ sales on the report")]
-    public void ThenThereShouldBePizzaSalesOnTheReport(
-        decimal pepperoniSales, decimal margheritaSales, decimal bbqSales)
-    {
-        void AssertPizzaSales(string pizzaName, decimal expectedSales)
-        {
-            Assert.IsTrue(
-                generatedReport!.SalesByPizza.TryGetValue(pizzaName, out var pizzaValue),
-                $"Sales data for pizza '{pizzaName}' was not found in the report.");
-            Assert.AreEqual(expectedSales, pizzaValue!.Sales, $"Unexpected sales for '{pizzaName}'.");
-        }
-
-        Assert.IsNotNull(generatedReport, "Expected a generated report but it was null.");
-        AssertPizzaSales("Pepperoni", pepperoniSales);
-        AssertPizzaSales("Margherita", margheritaSales);
-        AssertPizzaSales("BBQ", bbqSales);
-    }
-
-    [Then("there should be a by day breakdown on the report")]
-    public void ThenThereShouldBeAByDayBreakdownOnTheReport()
-    {
-        Assert.IsNotNull(generatedReport, "Expected a generated report but it was null.");
-        Assert.HasCount(7, generatedReport!.SalesByDay, $"Expected sales data for 7 days but found {generatedReport.SalesByDay.Count}.");
-    }
-
-    [Then("all values should be also shown as percentages of the total")]
-    public void ThenAllValuesShouldBeAlsoShownAsPercentagesOfTheTotal()
-    {
-        Assert.IsNotNull(generatedReport, "Expected a generated report but it was null.");
-        Assert.AreEqual(100, generatedReport!.TotalSales.Percentage, $"Expected total sales percentage to be 100% but got {generatedReport.TotalSales.Percentage}%");
-        foreach (var pizzaReport in generatedReport.SalesByPizza)
-        {
-            Assert.IsTrue(pizzaReport.Value.Percentage is >= 0 and <= 100, $"Expected percentage for {pizzaReport.Key} to be between 0% and 100% but got {pizzaReport.Value.Percentage}%");
-        }
-        foreach (var dayReport in generatedReport.SalesByDay)
-        {
-            Assert.IsTrue(dayReport.Value.Percentage is >= 0 and <= 100, $"Expected percentage for {dayReport.Key} to be between 0% and 100% but got {dayReport.Value.Percentage}%");
-        }
+        string actualReport = ReportPrinter.PrintReport(generatedReport ?? throw new InvalidOperationException("Report is not generated."));
+        string diff = UnidiffRenderer.GenerateUnidiff(
+            expectedReport, actualReport, "expected", "actual");
+        Assert.IsTrue(string.IsNullOrEmpty(diff),
+            "The report is different from the expected" + Environment.NewLine + diff);
     }
 }
```

### WIMP.Specs/Support/ReportPrinter.cs

[View file](After/WIMP.Specs/Support/ReportPrinter.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/ReportPrinter.cs#L1-L32)</sub>

```diff
@@ -0,0 +1,32 @@
+using System.Text;
+
+using WIMP.App.Models;
+
+namespace WIMP.Specs.Support;
+
+public static class ReportPrinter
+{
+    public static string PrintReport(SalesReport report)
+    {
+        var result = new StringBuilder();
+
+        result.AppendLine("* By Pizza");
+        foreach (var pizzaReport in report.SalesByPizza.OrderBy(e => e.Key))
+        {
+            result.AppendLine($"  * {pizzaReport.Key}: {FormatValue(pizzaReport.Value)}");
+        }
+
+        result.AppendLine("* By Day");
+        foreach (var dayReport in report.SalesByDay.OrderBy(e => e.Key))
+        {
+            result.AppendLine($"  * {dayReport.Key:yyyy-MM-dd}: {FormatValue(dayReport.Value)}");
+        }
+
+        result.AppendLine($"* Total: {FormatValue(report.TotalSales)}");
+
+        return result.ToString().TrimEnd();
+    }
+
+    private static string FormatValue(SalesReportValue value) =>
+        $"${value.Sales:0.##} ({value.Percentage:0}%)";
+}
```

### WIMP.Specs/WIMP.Specs.csproj

[View file](After/WIMP.Specs/WIMP.Specs.csproj#L18)

<sub>[Jump to change](After/WIMP.Specs/WIMP.Specs.csproj#L21)</sub>

```diff
@@ -18,6 +18,7 @@
   </ItemGroup>
 
   <ItemGroup>
+    <PackageReference Include="DiffPlex" Version="1.9.0" />
     <PackageReference Include="Microsoft.AspNetCore.Mvc.Testing" Version="9.0.4" />
     <PackageReference Include="Microsoft.NET.Test.Sdk" Version="18.0.1" />
     <PackageReference Include="MSTest.TestAdapter" Version="4.0.2" />
```
