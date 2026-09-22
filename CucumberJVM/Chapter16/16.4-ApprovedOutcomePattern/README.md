# Pattern Differences: 16.4-ApprovedOutcomePattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsreportingstepdefinitionsjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/ReportPrinter.java](#srctestjavacomwimpappspecssupportreportprinterjava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Reporting.feature](#srctestresourcescomwimpappspecsreportingfeature)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L3)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L6)</sub>

```diff
@@ -3,15 +3,14 @@ package com.wimp.app.specs.stepdefinitions;
 import com.wimp.app.models.SalesReport;
 import com.wimp.app.specs.drivers.BackdoorApiDriver;
 import com.wimp.app.specs.drivers.ReportingApiDriver;
+import com.wimp.app.specs.support.ReportPrinter;
 import io.cucumber.datatable.DataTable;
 import io.cucumber.java.en.*;
 
-import java.math.BigDecimal;
 import java.time.LocalDate;
 import java.util.*;
 
 import static org.assertj.core.api.Assertions.assertThat;
-import static org.junit.jupiter.api.Assertions.*;
 
 public class ReportingStepDefinitions {
     private final BackdoorApiDriver backdoorApiDriver;
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L35-L39)</sub>

```diff
@@ -33,48 +32,10 @@ public class ReportingStepDefinitions {
         generatedReport = reportingApiDriver.generateSalesReport(startDay).execute();
     }
 
-    @Then("the report should show a total sales volume of {price}")
-    public void theReportShouldShowATotalSalesVolumeOf(BigDecimal expectedTotalSales) {
-        assertNotNull(generatedReport, "Report is not generated.");
-        assertThat(generatedReport.totalSales().sales()).as("Unexpected total sales.").isEqualByComparingTo(expectedTotalSales);
-    }
-
-    private void assertPizzaSales(String pizzaName, BigDecimal expectedSales) {
-        assertTrue(
-            generatedReport.salesByPizza().containsKey(pizzaName),
-            "Sales data for pizza '%s' was not found in the report.".formatted(pizzaName));
-        assertThat(generatedReport.salesByPizza().get(pizzaName).sales()).as("Unexpected sales for '%s'.".formatted(pizzaName)).isEqualByComparingTo(expectedSales);
-    }
-
-    @And("there should be {price} Pepperoni, {price} Margherita, and {price} BBQ sales on the report")
-    public void thereShouldBe$Pepperoni$MargheritaAnd$BBQSalesOnTheReport(
-        BigDecimal pepperoniSales, BigDecimal margheritaSales, BigDecimal bbqSales) {
-
-        assertNotNull(generatedReport, "Report is not generated.");
-        assertPizzaSales("Pepperoni", pepperoniSales);
-        assertPizzaSales("Margherita", margheritaSales);
-        assertPizzaSales("BBQ", bbqSales);
-    }
-
-    @And("there should be a by day breakdown on the report")
-    public void thereShouldBeAByDayBreakdownOnTheReport() {
-        assertNotNull(generatedReport, "Report is not generated.");
-        assertEquals(7, generatedReport.salesByDay().size(), "Expected sales data for 7 days.");
-    }
-
-    @And("all values should be also shown as percentages of the total")
-    public void allValuesShouldBeAlsoShownAsPercentagesOfTheTotal() {
-        assertNotNull(generatedReport, "Report is not generated.");
-        assertThat(generatedReport.totalSales().percentage()).as("Expected total sales percentage to be 100%").isEqualByComparingTo(new BigDecimal(100));
-        for (var pizzaReport: generatedReport.salesByPizza().entrySet())
-        {
-            assertThat(pizzaReport.getValue().percentage()).as("Expected percentage for %s to be between 0%% and 100%%".formatted(pizzaReport.getKey()))
-                .isBetween(BigDecimal.ZERO, new BigDecimal(100));
-        }
-        for (var dayReport: generatedReport.salesByDay().entrySet())
-        {
-            assertThat(dayReport.getValue().percentage()).as("Expected percentage for %s to be between 0%% and 100%%".formatted(dayReport.getKey()))
-                .isBetween(BigDecimal.ZERO, new BigDecimal(100));
-        }
+    @Then("the report should show:")
+    public void theReportShouldShow(String expectedReport) {
+        assertThat(ReportPrinter.printReport(generatedReport).trim().lines().toList())
+            .as("The printed report did not match the expected output.")
+            .containsExactlyElementsOf(expectedReport.trim().lines().toList());
     }
 }
```

### src/test/java/com/wimp/app/specs/support/ReportPrinter.java

[View file](After/src/test/java/com/wimp/app/specs/support/ReportPrinter.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/ReportPrinter.java#L1-L50)</sub>

```diff
@@ -0,0 +1,50 @@
+package com.wimp.app.specs.support;
+
+import java.math.RoundingMode;
+import java.text.DecimalFormat;
+import java.time.format.DateTimeFormatter;
+import java.util.Map;
+import com.wimp.app.models.SalesReport;
+import com.wimp.app.models.SalesReportValue;
+
+public final class ReportPrinter {
+
+    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
+    private static final DecimalFormat SALES_FORMATTER = new DecimalFormat("0.##");
+    private static final DecimalFormat PERCENT_FORMATTER;
+
+    static {
+        PERCENT_FORMATTER = new DecimalFormat("0");
+        PERCENT_FORMATTER.setRoundingMode(RoundingMode.HALF_UP);
+    }
+
+    private ReportPrinter() {
+        // Prevent instantiation for static utility class
+    }
+
+    public static String printReport(SalesReport report) {
+        StringBuilder result = new StringBuilder();
+
+        result.append("* By Pizza\n");
+        report.salesByPizza().entrySet().stream()
+            .sorted(Map.Entry.comparingByKey())
+            .forEach(entry -> result.append(String.format("  * %s: %s\n",
+                entry.getKey(), formatValue(entry.getValue()))));
+
+        result.append("* By Day\n");
+        report.salesByDay().entrySet().stream()
+            .sorted(Map.Entry.comparingByKey())
+            .forEach(entry -> result.append(String.format("  * %s: %s\n",
+                DATE_FORMATTER.format(entry.getKey()), formatValue(entry.getValue()))));
+
+        result.append(String.format("* Total: %s\n", formatValue(report.totalSales())));
+
+        return result.toString().stripTrailing();
+    }
+
+    private static String formatValue(SalesReportValue value) {
+        return String.format("$%s (%s%%)",
+            SALES_FORMATTER.format(value.sales()),
+            PERCENT_FORMATTER.format(value.percentage()));
+    }
+}
```

### src/test/resources/com/wimp/app/specs/Reporting.feature

[View file](After/src/test/resources/com/wimp/app/specs/Reporting.feature#L14)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Reporting.feature#L17-L32)</sub>

```diff
@@ -14,7 +14,19 @@ Rule: Weekly report can be generated with daily and by pizza breakdown
       | 2026-03-15 | $600 | $200      | $400       |
     And the restaurant owner is authenticated
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
