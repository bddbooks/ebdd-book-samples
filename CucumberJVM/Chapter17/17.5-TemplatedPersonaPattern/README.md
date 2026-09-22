# Pattern Differences: 17.5-TemplatedPersonaPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsreportingstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java](#srctestjavacomwimpappspecssupportcustomparametertypesjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/SalesFilterData.java](#srctestjavacomwimpappspecssupportsalesfilterdatajava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Reporting.feature](#srctestresourcescomwimpappspecsreportingfeature)
- ➖ Deleted [src/test/resources/com/wimp/app/specs/realistic-truffle-shortage-traffic.csv](#srctestresourcescomwimpappspecsrealistictruffleshortagetrafficcsv)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L5)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L8)</sub>

```diff
@@ -5,6 +5,7 @@ import com.wimp.app.models.SalesReport;
 import com.wimp.app.specs.drivers.BackdoorApiDriver;
 import com.wimp.app.specs.drivers.ReportingApiDriver;
 import com.wimp.app.specs.support.ReportPrinter;
+import com.wimp.app.specs.support.SalesFilterData;
 import com.wimp.app.specs.support.TestFileSystem;
 import io.cucumber.java.en.*;
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L34-L52)</sub>

```diff
@@ -30,19 +31,25 @@ public class ReportingStepDefinitions {
 
     @Given("sales traffic from {string}")
     public void salesTrafficFrom(String fileName) throws Exception {
-        loadTrafficData(fileName);
+        loadTrafficData(fileName, List.of());
     }
 
-    private void loadTrafficData(String fileName) throws Exception {
-        // A detailed discussion of the TestFileSystem class can be found in chapter 21: Test File System pattern
+    @Given("sales traffic from {string} with")
+    public void salesTrafficFromWith(String fileName, List<SalesFilterData> filterTable) throws Exception {
+        loadTrafficData(fileName, filterTable);
+    }
+
+    private void loadTrafficData(String fileName, List<SalesFilterData> filterTable) throws Exception {
         Path path = Path.of(testFileSystem.getFeatureInputFolder(), fileName);
         List<String> lines = Files.readAllLines(path);
         if (lines.size() < 2) throw new IllegalArgumentException("The CSV file '" + path + "' was empty!");
         var headers = Arrays.asList(lines.getFirst().split(","));
         List<DailyPizzaSales> sales = new ArrayList<>();
+        boolean excludeTruffle = filterTable.stream().anyMatch(f -> "truffle sales".equals(f.excludeSection()));
         for (String line : lines.subList(1, lines.size())) {
             String[] values = line.split(",");
             String pizzaName = values[headers.indexOf("Pizza")];
+            if (excludeTruffle && pizzaName.equals("Truffle Bliss")) continue;
             DailyPizzaSales item = new DailyPizzaSales();
             item.setDate(LocalDate.parse(values[headers.indexOf("Date")]));
             item.setPizza(pizzaName);
```

### src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java

[View file](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L3-L16)</sub>

```diff
@@ -1,11 +1,19 @@
 package com.wimp.app.specs.support;
 
+import io.cucumber.java.DataTableType;
 import io.cucumber.java.ParameterType;
 
 import java.time.LocalDate;
+import java.util.Map;
 
 public class CustomParameterTypes {
 
+    @DataTableType
+    public SalesFilterData salesFilterDataRow(Map<String, String> row) {
+        return new SalesFilterData(
+            row.get("exclude section"));
+    }
+
     @ParameterType("\\d{4}-\\d{2}-\\d{2}")
     public LocalDate date(String value) {
         return LocalDate.parse(value);
```

### src/test/java/com/wimp/app/specs/support/SalesFilterData.java

[View file](After/src/test/java/com/wimp/app/specs/support/SalesFilterData.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/SalesFilterData.java#L1-L4)</sub>

```diff
@@ -0,0 +1,4 @@
+package com.wimp.app.specs.support;
+
+public record SalesFilterData(String excludeSection) {
+}
```

### src/test/resources/com/wimp/app/specs/Reporting.feature

[View file](After/src/test/resources/com/wimp/app/specs/Reporting.feature#L27)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Reporting.feature#L30-L32)</sub>

```diff
@@ -27,7 +27,9 @@ Rule: Weekly sales report can be generated with daily and by pizza breakdown
 Rule: Ingredient usage report can be generated for a week
 
   Scenario: No sale of a particular ingredient on a week
-    Given sales traffic from "realistic-truffle-shortage-traffic.csv"
+    Given sales traffic from "realistic-traffic.csv" with
+      | exclude section|
+      | truffle sales  |
     And the restaurant owner is authenticated
     When the ingredient usage report is requested for the week beginning 2026-03-09
     Then the ingredient usage report should contain "truffle" usage as 0 portions
```

### src/test/resources/com/wimp/app/specs/realistic-truffle-shortage-traffic.csv

[View file](After/src/test/resources/com/wimp/app/specs/realistic-truffle-shortage-traffic.csv#L0)

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
