# Pattern Differences: 17.2-InPlacePersonaPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsreportingstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java](#srctestjavacomwimpappspecssupportcustomparametertypesjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/NamedOrderData.java](#srctestjavacomwimpappspecssupportnamedorderdatajava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/OrderingContext.java](#srctestjavacomwimpappspecssupportorderingcontextjava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Ordering.feature](#srctestresourcescomwimpappspecsorderingfeature)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Reporting.feature](#srctestresourcescomwimpappspecsreportingfeature)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L3)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L6-L7)</sub>

```diff
@@ -3,7 +3,8 @@ package com.wimp.app.specs.stepdefinitions;
 import com.wimp.app.specs.drivers.BackdoorApiDriver;
 import com.wimp.app.specs.drivers.OrderingApiDriver;
 import com.wimp.app.specs.support.DomainDefaults;
-import com.wimp.app.specs.support.OrderByNumberData;
+import com.wimp.app.specs.support.NamedOrderData;
+import com.wimp.app.specs.support.OrderingContext;
 import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
 import io.cucumber.java.en.*;
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L16-L33)</sub>

```diff
@@ -12,21 +13,24 @@ import java.util.List;
 import static org.junit.jupiter.api.Assertions.assertEquals;
 
 public class OrderingStepDefinitions {
+    private final OrderingContext orderingContext;
     private final OrderingApiDriver orderingApiDriver;
     private final BackdoorApiDriver backdoorApiDriver;
 
-    public OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, BackdoorApiDriver backdoorApiDriver) {
+    public OrderingStepDefinitions(OrderingContext orderingContext, OrderingApiDriver orderingApiDriver, BackdoorApiDriver backdoorApiDriver) {
+        this.orderingContext = orderingContext;
         this.orderingApiDriver = orderingApiDriver;
         this.backdoorApiDriver = backdoorApiDriver;
     }
 
     @Given("the customer has the following orders")
-    public void theCustomerHasTheFollowingOrders(List<OrderByNumberData> orders) throws Exception {
-        for (final OrderByNumberData order : orders) {
+    public void theCustomerHasTheFollowingOrders(List<NamedOrderData> orders) throws Exception {
+        for (final NamedOrderData order : orders) {
             var orderRequest = new PlaceOrderRequestObjectMother().build();
-            backdoorApiDriver
+            var placedOrder = backdoorApiDriver
                 .prepareOrder(DomainDefaults.CUSTOMER_NAME, orderRequest, order.getStatus())
                 .execute();
+            orderingContext.getNamedOrders().put(order.getOrderName(), placedOrder.getOrderNo());
         }
     }
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L43-L51)</sub>

```diff
@@ -36,10 +40,15 @@ public class OrderingStepDefinitions {
     }
 
     @Then("their order list should contain")
-    public void thenTheirOrderListShouldContain(List<OrderByNumberData> expectedOrders) throws Exception {
-        for (final OrderByNumberData expectedOrder : expectedOrders) {
-            var order = orderingApiDriver.getOrder(expectedOrder.getOrderNo()).execute();
-            assertEquals(expectedOrder.getStatus(), order.getStatus(), "Unexpected status for order %s.".formatted(expectedOrder.getOrderNo()));
+    public void thenTheirOrderListShouldContain(List<NamedOrderData> expectedOrders) throws Exception {
+        for (final NamedOrderData expectedOrder : expectedOrders) {
+            var orderNo = orderingContext.getNamedOrders().getOrDefault(expectedOrder.getOrderName(), null);
+            if (orderNo == null) {
+                throw new RuntimeException("Order %s not known".formatted(expectedOrder.getOrderName()));
+            }
+
+            var order = orderingApiDriver.getOrder(orderNo).execute();
+            assertEquals(expectedOrder.getStatus(), order.getStatus(), "Unexpected status for order %s.".formatted(expectedOrder.getOrderName()));
         }
     }
 }
```

### src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L15)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L18)</sub>

```diff
@@ -15,6 +15,7 @@ import static org.assertj.core.api.Assertions.assertThat;
 public class ReportingStepDefinitions {
     private final BackdoorApiDriver backdoorApiDriver;
     private final ReportingApiDriver reportingApiDriver;
+    private final Map<String, DataTable> salesTrafficSamples = new HashMap<>();
     private SalesReport generatedReport;
 
     public ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver) {
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L26-L34)</sub>

```diff
@@ -22,8 +23,15 @@ public class ReportingStepDefinitions {
         this.reportingApiDriver = reportingApiDriver;
     }
 
-    @Given("a week of operations with these pizza sales:")
-    public void aWeekOfOperationsWithThesePizzaSales(DataTable salesTraffic) throws Exception {
+    @Given("the {string} weekly sales traffic is")
+    public void theWeeklySalesTrafficIs(String name, DataTable salesTraffic) {
+        salesTrafficSamples.put(name, salesTraffic);
+    }
+
+    @Given("the {string} weekly sales traffic")
+    public void theWeeklySalesTraffic(String name) throws Exception {
+        var salesTraffic = Optional.ofNullable(salesTrafficSamples.get(name))
+            .orElseThrow(() -> new IllegalArgumentException("Traffic " + name + " not known"));
         backdoorApiDriver.prepareSalesTraffic(salesTraffic).execute();
     }
 
```

### src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java

[View file](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L6)

```diff
@@ -6,7 +6,6 @@ import io.cucumber.java.ParameterType;
 
 import java.math.BigDecimal;
 import java.time.LocalDate;
-import java.time.LocalTime;
 import java.util.Map;
 
 public class CustomParameterTypes {
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L19-L39)</sub>

```diff
@@ -17,22 +16,27 @@ public class CustomParameterTypes {
         this.orderingContext = orderingContext;
     }
 
-    @ParameterType("the order|the placed order|order \\d+")
+    @ParameterType("the order|the placed order|order [A-Z]")
     public int order(String value) {
         if (value.matches("the order|the placed order")){
             return orderingContext.getCurrentOrderNoVerified();
         }
-        if (value.matches("order \\d+")){
-            return Integer.parseInt(value.substring("order ".length()));
+        if (value.matches("order [A-Z]")){
+            String name = value.substring(value.length() - 1);
+            Integer orderNo = orderingContext.getNamedOrders().get(name);
+            if (orderNo == null) {
+                throw new RuntimeException("Order " + name + " not known");
+            }
+            return orderNo;
         }
         throw new RuntimeException("Invalid order value: " + value);
     }
 
     @DataTableType
-    public OrderByNumberData orderByNumberDataRow(Map<String, String> row) {
-        var orderData = new OrderByNumberData();
-        if (row.containsKey("order no"))
-            orderData.setOrderNo(Integer.parseInt(row.get("order no")));
+    public NamedOrderData namedOrderDataRow(Map<String, String> row) {
+        var orderData = new NamedOrderData();
+        if (row.containsKey("order name"))
+            orderData.setOrderName(row.get("order name"));
         if (row.containsKey("status"))
             orderData.setStatus(OrderStatus.valueOf(row.get("status").toUpperCase()));
         return orderData;
```

### src/test/java/com/wimp/app/specs/support/NamedOrderData.java

[View file](After/src/test/java/com/wimp/app/specs/support/NamedOrderData.java#L2)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/NamedOrderData.java#L5-L14)</sub>

```diff
@@ -2,16 +2,16 @@ package com.wimp.app.specs.support;
 
 import com.wimp.app.models.OrderStatus;
 
-public class OrderByNumberData {
-    private Integer orderNo;
+public class NamedOrderData {
+    private String orderName = "";
     private OrderStatus status = OrderStatus.PLACED;
 
-    public Integer getOrderNo() {
-        return orderNo;
+    public String getOrderName() {
+        return orderName;
     }
 
-    public void setOrderNo(Integer orderNo) {
-        this.orderNo = orderNo;
+    public void setOrderName(String orderName) {
+        this.orderName = orderName;
     }
 
     public OrderStatus getStatus() {
```

### src/test/java/com/wimp/app/specs/support/OrderingContext.java

[View file](After/src/test/java/com/wimp/app/specs/support/OrderingContext.java#L9)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/OrderingContext.java#L12)</sub>

```diff
@@ -9,6 +9,7 @@ import java.util.*;
 @ScenarioScope
 public class OrderingContext {
     private Integer currentOrderNo;
+    private final Map<String, Integer> namedOrders = new HashMap<>();
 
     public Integer getCurrentOrderNo() {
         return currentOrderNo;
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/OrderingContext.java#L26-L29)</sub>

```diff
@@ -22,4 +23,8 @@ public class OrderingContext {
     public void setCurrentOrderNo(Integer currentOrderNo) {
         this.currentOrderNo = currentOrderNo;
     }
+
+    public Map<String, Integer> getNamedOrders() {
+        return namedOrders;
+    }
 }
```

### src/test/resources/com/wimp/app/specs/Ordering.feature

[View file](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L4)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L7-L17)</sub>

```diff
@@ -4,14 +4,14 @@ Rule: Open orders can be cancelled
 
   Scenario: Customer cancels one of their open orders
     Given the customer has the following orders
-      | order no | status    |
-      | 1        | Completed |
-      | 2        | Placed    |
-      | 3        | Placed    |
+      | order name | status    |
+      | A          | Completed |
+      | B          | Placed    |
+      | C          | Placed    |
     And they are authenticated
-    When they cancel order 2
+    When they cancel order B
     Then their order list should contain
-      | order no | status    |
-      | 1        | Completed |
-      | 2        | Cancelled |
-      | 3        | Placed    |
+      | order name | status    |
+      | A          | Completed |
+      | B          | Cancelled |
+      | C          | Placed    |
```

### src/test/resources/com/wimp/app/specs/Reporting.feature

[View file](After/src/test/resources/com/wimp/app/specs/Reporting.feature#L1)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Reporting.feature#L3-L35)</sub>

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

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Reporting.feature#L57)</sub>

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

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Reporting.feature#L79)</sub>

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

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Reporting.feature#L103)</sub>

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
