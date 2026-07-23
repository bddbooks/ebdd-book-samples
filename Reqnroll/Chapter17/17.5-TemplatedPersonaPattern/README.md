# Pattern Differences: 17.5-TemplatedPersonaPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Features/Reporting.feature](#wimpspecsfeaturesreportingfeature)
- ➖ Deleted [WIMP.Specs/Features/realistic-truffle-shortage-traffic.csv](#wimpspecsfeaturesrealistictruffleshortagetrafficcsv)
- 📝 Modified [WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs](#wimpspecsstepdefinitionsreportingstepdefinitionscs)

## Detailed Changes

### WIMP.Specs/Features/Reporting.feature

[View file](After/WIMP.Specs/Features/Reporting.feature#L27)

<sub>[Jump to change](After/WIMP.Specs/Features/Reporting.feature#L30-L32)</sub>

```diff
@@ -27,7 +27,9 @@ Rule: Weekly sales report can be generated with daily and by pizza breakdown
 Rule: Ingredient usage report can be generated for a week
 
   Scenario: No sale of a particular ingredient on a week
-    Given sales traffic from "realistic-truffle-shortage-traffic.csv"
+    Given sales traffic from "realistic-traffic.csv" with
+      | exclude section|
+      | truffle sales  |
     And the restaurant owner is logged in
     When the ingredient usage report is requested for the week beginning 2026-03-09
     Then the ingredient usage report should contain "truffle" usage as 0 portions
```

### WIMP.Specs/Features/realistic-truffle-shortage-traffic.csv

[View file](After/WIMP.Specs/Features/realistic-truffle-shortage-traffic.csv#L0)

```diff
@@ -1,29 +0,0 @@
-Date,Pizza,Sales
-2026-03-09,Truffle Bliss,200
-2026-03-09,Margherita,300
-2026-03-09,BBQ,100
-2026-03-09,Pepperoni,200
-2026-03-10,BBQ,300
-2026-03-10,Pepperoni,0
-2026-03-10,Margherita,300
-2026-03-10,Truffle Bliss,100
-2026-03-11,BBQ,200
-2026-03-11,Pepperoni,200
-2026-03-11,Margherita,200
-2026-03-11,Truffle Bliss,100
-2026-03-12,BBQ,0
-2026-03-12,Pepperoni,100
-2026-03-12,Margherita,200
-2026-03-12,Truffle Bliss,200
-2026-03-13,BBQ,200
-2026-03-13,Pepperoni,200
-2026-03-13,Margherita,500
-2026-03-13,Truffle Bliss,300
-2026-03-14,BBQ,700
-2026-03-14,Pepperoni,300
-2026-03-14,Margherita,800
-2026-03-14,Truffle Bliss,500
-2026-03-15,BBQ,600
-2026-03-15,Pepperoni,200
-2026-03-15,Margherita,400
-2026-03-15,Truffle Bliss,600
```

### WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L16)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L19-L38)</sub>

```diff
@@ -16,15 +16,26 @@ namespace WIMP.Specs.StepDefinitions;
 public class ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver,
     IFeatureContext featureContext)
 {
+    public class SalesFilterData
+    {
+        public required string ExcludeSection { get; set; }
+    }
+
     private SalesReport? generatedReport;
 
     [Given("sales traffic from {string}")]
     public async Task GivenSalesTrafficFrom(string fileName)
     {
-        await LoadTrafficData(fileName);
+        await LoadTrafficData(fileName, []);
     }
 
-    private async Task LoadTrafficData(string fileName)
+    [Given("sales traffic from {string} with")]
+    public async Task GivenSalesTrafficFrom(string fileName, DataTable filterTable)
+    {
+        await LoadTrafficData(fileName, filterTable.CreateSet<SalesFilterData>());
+    }
+
+    private async Task LoadTrafficData(string fileName, IEnumerable<SalesFilterData> filterData)
     {
         string csvFilePath = Path.Combine(
             featureContext.FeatureInfo.FolderPath, fileName);
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L47-L51)</sub>

```diff
@@ -33,6 +44,11 @@ public class ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, Repor
         var salesTraffic =
             csv.GetRecords<DailyPizzaSales>();
 
+        if (filterData.Any(fd => fd.ExcludeSection == "truffle sales"))
+        {
+            salesTraffic = salesTraffic.Where(r => r.Pizza != "Truffle Bliss");
+        }
+
         await backdoorApiDriver
             .PrepareSalesTraffic(salesTraffic)
             .Execute();
```
