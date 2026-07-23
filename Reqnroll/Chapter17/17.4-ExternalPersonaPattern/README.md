# Pattern Differences: 17.4-ExternalPersonaPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Features/Reporting.feature](#wimpspecsfeaturesreportingfeature)
- ➕ Added [WIMP.Specs/Features/realistic-traffic.csv](#wimpspecsfeaturesrealistictrafficcsv)
- 📝 Modified [WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs](#wimpspecsstepdefinitionsreportingstepdefinitionscs)
- 📝 Modified [WIMP.Specs/WIMP.Specs.csproj](#wimpspecswimpspecscsproj)

## Detailed Changes

### WIMP.Specs/Features/Reporting.feature

[View file](After/WIMP.Specs/Features/Reporting.feature#L2)

<sub>[Jump to change](After/WIMP.Specs/Features/Reporting.feature#L5-L6)</sub>

```diff
@@ -2,16 +2,8 @@ Feature: Reporting
 
 Rule: Weekly sales report can be generated with daily and by pizza breakdown
 
-  Scenario: Weekly sales report shows totals by pizza and day
-    Given a week of operations with these pizza sales:
-      | date       | BBQ  | Pepperoni | Margherita | Truffle Bliss |
-      | 2026-03-09 | $100 | $200      | $300       | $200          |
-      | 2026-03-10 | $300 | $0        | $300       | $100          |
-      | 2026-03-11 | $200 | $200      | $200       | $100          |
-      | 2026-03-12 | $0   | $100      | $200       | $200          |
-      | 2026-03-13 | $200 | $200      | $500       | $300          |
-      | 2026-03-14 | $700 | $300      | $800       | $500          |
-      | 2026-03-15 | $600 | $200      | $400       | $600          |
+  Scenario: Weekly sales report provided for a realistic traffic
+    Given sales traffic from "realistic-traffic.csv"
     And the restaurant owner is logged in
     When the sales report is requested for the week beginning 2026-03-09
     Then the report should show:
```

### WIMP.Specs/Features/realistic-traffic.csv

[View file](After/WIMP.Specs/Features/realistic-traffic.csv#L1)

<sub>[Jump to change](After/WIMP.Specs/Features/realistic-traffic.csv#L1-L29)</sub>

```diff
@@ -0,0 +1,29 @@
+Date,Pizza,Sales
+2026-03-09,Truffle Bliss,200
+2026-03-09,Margherita,300
+2026-03-09,BBQ,100
+2026-03-09,Pepperoni,200
+2026-03-10,BBQ,300
+2026-03-10,Pepperoni,0
+2026-03-10,Margherita,300
+2026-03-10,Truffle Bliss,100
+2026-03-11,BBQ,200
+2026-03-11,Pepperoni,200
+2026-03-11,Margherita,200
+2026-03-11,Truffle Bliss,100
+2026-03-12,BBQ,0
+2026-03-12,Pepperoni,100
+2026-03-12,Margherita,200
+2026-03-12,Truffle Bliss,200
+2026-03-13,BBQ,200
+2026-03-13,Pepperoni,200
+2026-03-13,Margherita,500
+2026-03-13,Truffle Bliss,300
+2026-03-14,BBQ,700
+2026-03-14,Pepperoni,300
+2026-03-14,Margherita,800
+2026-03-14,Truffle Bliss,500
+2026-03-15,BBQ,600
+2026-03-15,Pepperoni,200
+2026-03-15,Margherita,400
+2026-03-15,Truffle Bliss,600
```

### WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L1-L4)</sub>

```diff
@@ -1,3 +1,7 @@
+using System.Globalization;
+
+using CsvHelper;
+
 using DiffPlex.Renderer;
 
 using Reqnroll;
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L16-L35)</sub>

```diff
@@ -9,10 +13,26 @@ using WIMP.Specs.Support;
 namespace WIMP.Specs.StepDefinitions;
 
 [Binding]
-public class ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver)
+public class ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver,
+    IFeatureContext featureContext)
 {
     private SalesReport? generatedReport;
 
+    [Given("sales traffic from {string}")]
+    public async Task GivenSalesTrafficFrom(string fileName)
+    {
+        string csvFilePath = Path.Combine(
+            featureContext.FeatureInfo.FolderPath, fileName);
+        using var reader = new StreamReader(csvFilePath);
+        using var csv = new CsvReader(reader, CultureInfo.InvariantCulture);
+        var salesTraffic =
+            csv.GetRecords<DailyPizzaSales>();
+
+        await backdoorApiDriver
+            .PrepareSalesTraffic(salesTraffic)
+            .Execute();
+    }
+
     [Given("a week of operations with these pizza sales:")]
     public async Task GivenAWeekOfOperationsWithThesePizzaSales(DataTable salesTable)
     {
```

### WIMP.Specs/WIMP.Specs.csproj

[View file](After/WIMP.Specs/WIMP.Specs.csproj#L19)

<sub>[Jump to change](After/WIMP.Specs/WIMP.Specs.csproj#L22)</sub>

```diff
@@ -19,6 +19,7 @@
 
   <ItemGroup>
     <PackageReference Include="DiffPlex" Version="1.9.0" />
+    <PackageReference Include="CsvHelper" Version="33.1.0" />
 
     <PackageReference Include="Microsoft.AspNetCore.Mvc.Testing" Version="9.0.4" />
     <PackageReference Include="Microsoft.NET.Test.Sdk" Version="18.0.1" />
```

<sub>[Jump to change](After/WIMP.Specs/WIMP.Specs.csproj#L33-L39)</sub>

```diff
@@ -29,4 +30,11 @@
     <PackageReference Include="Reqnroll.MsTest" Version="3.3.3" />
   </ItemGroup>
 
+  <ItemGroup>
+    <!-- all CSV content file within Features is copied to the output -->
+    <None Update="Features\**\*.csv">
+      <CopyToOutputDirectory>PreserveNewest</CopyToOutputDirectory>
+    </None>
+  </ItemGroup>
+
 </Project>
```
