# Pattern Differences: 16.3-EnsurePattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/CustomerCollectionStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionscustomercollectionstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/OrderingContext.java](#srctestjavacomwimpappspecssupportorderingcontextjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/CustomerCollectionStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/CustomerCollectionStepDefinitions.java#L4)

```diff
@@ -4,7 +4,6 @@ import com.wimp.app.models.OrderCollectionDetails;
 import com.wimp.app.specs.drivers.OrderingApiDriver;
 import com.wimp.app.specs.support.DataTableDiffHelper;
 import com.wimp.app.specs.support.OrderingContext;
-import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
 import io.cucumber.datatable.DataTable;
 import io.cucumber.java.en.*;
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/CustomerCollectionStepDefinitions.java#L26-L27)</sub>

```diff
@@ -24,12 +23,8 @@ public class CustomerCollectionStepDefinitions {
 
     @When("they choose to collect their order")
     public void theyChooseToCollectTheirOrder() throws Exception {
-        if (orderingContext.getCurrentOrderNo() == null)
-        {
-            var orderRequest = new PlaceOrderRequestObjectMother().build();
-            var placedOrder = orderingApiDriver.placeOrder(orderRequest).execute();
-            orderingContext.setCurrentOrderNo(placedOrder.getOrderNo());
-        }
+        orderingContext.ensureOrderPlaced();
+
         orderCollectionDetails = orderingApiDriver.setForCollection(orderingContext.getCurrentOrderNoVerified()).execute();
     }
 
```

### src/test/java/com/wimp/app/specs/support/OrderingContext.java

[View file](After/src/test/java/com/wimp/app/specs/support/OrderingContext.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/OrderingContext.java#L3)</sub>

```diff
@@ -1,5 +1,6 @@
 package com.wimp.app.specs.support;
 
+import com.wimp.app.specs.drivers.OrderingApiDriver;
 import io.cucumber.spring.ScenarioScope;
 import org.springframework.stereotype.Component;
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/OrderingContext.java#L12-L19)</sub>

```diff
@@ -8,9 +9,14 @@ import java.util.*;
 @Component
 @ScenarioScope
 public class OrderingContext {
+    private final OrderingApiDriver orderingApiDriver;
 
     private Integer currentOrderNo;
 
+    public OrderingContext(OrderingApiDriver orderingApiDriver) {
+        this.orderingApiDriver = orderingApiDriver;
+    }
+
     public Integer getCurrentOrderNo() {
         return currentOrderNo;
     }
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/OrderingContext.java#L32-L39)</sub>

```diff
@@ -23,4 +29,12 @@ public class OrderingContext {
     public void setCurrentOrderNo(Integer currentOrderNo) {
         this.currentOrderNo = currentOrderNo;
     }
+
+    public void ensureOrderPlaced() throws Exception {
+        if (currentOrderNo == null) {
+            var orderRequest = new PlaceOrderRequestObjectMother().build();
+            var placedOrder = orderingApiDriver.placeOrder(orderRequest).execute();
+            currentOrderNo = placedOrder.getOrderNo();
+        }
+    }
 }
```
