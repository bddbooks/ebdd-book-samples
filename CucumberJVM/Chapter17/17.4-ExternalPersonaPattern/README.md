# Pattern Differences: 17.4-ExternalPersonaPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsreportingstepdefinitionsjava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Reporting.feature](#srctestresourcescomwimpappspecsreportingfeature)
- ➕ Added [src/test/resources/com/wimp/app/specs/realistic-traffic.csv](#srctestresourcescomwimpappspecsrealistictrafficcsv)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L3-L14)</sub>

```diff
@@ -1,12 +1,17 @@
 package com.wimp.app.specs.stepdefinitions;
 
+import com.wimp.app.models.DailyPizzaSales;
 import com.wimp.app.models.SalesReport;
 import com.wimp.app.specs.drivers.BackdoorApiDriver;
 import com.wimp.app.specs.drivers.ReportingApiDriver;
 import com.wimp.app.specs.support.ReportPrinter;
+import com.wimp.app.specs.support.TestFileSystem;
 import io.cucumber.datatable.DataTable;
 import io.cucumber.java.en.*;
 
+import java.math.BigDecimal;
+import java.nio.file.Files;
+import java.nio.file.Path;
 import java.time.LocalDate;
 import java.util.*;
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L23-L49)</sub>

```diff
@@ -15,11 +20,33 @@ import static org.assertj.core.api.Assertions.assertThat;
 public class ReportingStepDefinitions {
     private final BackdoorApiDriver backdoorApiDriver;
     private final ReportingApiDriver reportingApiDriver;
+    private final TestFileSystem testFileSystem;
     private SalesReport generatedReport;
 
-    public ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver) {
+    public ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver, TestFileSystem testFileSystem) {
         this.backdoorApiDriver = backdoorApiDriver;
         this.reportingApiDriver = reportingApiDriver;
+        this.testFileSystem = testFileSystem;
+    }
+
+    @Given("sales traffic from {string}")
+    public void salesTrafficFrom(String fileName) throws Exception {
+        // A detailed discussion of the TestFileSystem class can be found in chapter 21: Test File System pattern
+        Path path = Path.of(testFileSystem.getFeatureInputFolder(), fileName);
+        List<String> lines = Files.readAllLines(path);
+        if (lines.size() < 2) throw new IllegalArgumentException("The CSV file '" + path + "' was empty!");
+        var headers = Arrays.asList(lines.getFirst().split(","));
+        List<DailyPizzaSales> sales = new ArrayList<>();
+        for (String line : lines.subList(1, lines.size())) {
+            String[] values = line.split(",");
+            String pizzaName = values[headers.indexOf("Pizza")];
+            DailyPizzaSales item = new DailyPizzaSales();
+            item.setDate(LocalDate.parse(values[headers.indexOf("Date")]));
+            item.setPizza(pizzaName);
+            item.setSales(new BigDecimal(values[headers.indexOf("Sales")]));
+            sales.add(item);
+        }
+        backdoorApiDriver.prepareSalesTraffic(sales).execute();
     }
 
     @Given("a week of operations with these pizza sales:")
```

### src/test/resources/com/wimp/app/specs/Reporting.feature

[View file](After/src/test/resources/com/wimp/app/specs/Reporting.feature#L2)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Reporting.feature#L5-L6)</sub>

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
     And the restaurant owner is authenticated
     When the sales report is requested for the week beginning 2026-03-09
     Then the report should show:
```

### src/test/resources/com/wimp/app/specs/realistic-traffic.csv

[View file](After/src/test/resources/com/wimp/app/specs/realistic-traffic.csv#L1)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/realistic-traffic.csv#L1-L29)</sub>

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
