# Pattern Differences: 13.3-IntroduceAndReferPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java](#srctestjavacomwimpappspecssupportcustomparametertypesjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/OrderingContext.java](#srctestjavacomwimpappspecssupportorderingcontextjava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Ordering.feature](#srctestresourcescomwimpappspecsorderingfeature)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L4)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L7)</sub>

```diff
@@ -4,6 +4,7 @@ import com.wimp.app.infrastructure.DataRepository;
 import com.wimp.app.services.NotificationService;
 import com.wimp.app.services.OrderService;
 import com.wimp.app.specs.support.AuthenticationContext;
+import com.wimp.app.specs.support.OrderingContext;
 import io.cucumber.java.Before;
 import io.cucumber.java.en.*;
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L15-L30)</sub>

```diff
@@ -11,21 +12,22 @@ import static org.junit.jupiter.api.Assertions.assertTrue;
 
 public class OrderingStepDefinitions {
     private final AuthenticationContext authenticationContext;
-    private int placedOrderNo;
+    private final OrderingContext orderingContext;
 
-    public OrderingStepDefinitions(AuthenticationContext authenticationContext) {
+    public OrderingStepDefinitions(AuthenticationContext authenticationContext, OrderingContext orderingContext) {
         this.authenticationContext = authenticationContext;
+        this.orderingContext = orderingContext;
     }
 
-    @Given("the authenticated customer has placed the order #{int}")
-    public void theAuthenticatedCustomerHasPlacedTheOrder(int orderNo) {
-        OrderService.placeOrder(authenticationContext.getAuthenticatedCustomerName(), "Margherita", orderNo);
-        placedOrderNo = orderNo;
+    @Given("the authenticated customer has placed an order")
+    public void theAuthenticatedCustomerHasPlacedAnOrder() {
+        var order = OrderService.placeOrder(authenticationContext.getAuthenticatedCustomerName(), "Margherita");
+        orderingContext.setCurrentOrderNo(order.getOrderNo());
     }
 
-    @When("the authenticated customer cancels the placed order")
-    public void theAuthenticatedCustomerCancelsThePlacedOrder() {
-        OrderService.cancelOrder(authenticationContext.getAuthenticatedCustomerName(), placedOrderNo);
+    @When("the authenticated customer cancels {order}")
+    public void theAuthenticatedCustomerCancelsTheOrder(int orderNo) {
+        OrderService.cancelOrder(authenticationContext.getAuthenticatedCustomerName(), orderNo);
     }
 
     @Then("the authenticated customer should receive a notification about the cancellation")
```

### src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java

[View file](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L1-L21)</sub>

```diff
@@ -0,0 +1,21 @@
+package com.wimp.app.specs.support;
+
+import com.wimp.app.models.*;
+import io.cucumber.java.ParameterType;
+
+import java.util.Optional;
+
+public class CustomParameterTypes {
+
+    private final OrderingContext orderingContext;
+
+    public CustomParameterTypes(OrderingContext orderingContext) {
+        this.orderingContext = orderingContext;
+    }
+
+    @ParameterType("the order|the placed order|the new order")
+    public int order(String value) {
+        return Optional.ofNullable(orderingContext.getCurrentOrderNo())
+            .orElseThrow(() -> new RuntimeException("No current order"));
+    }
+}
```

### src/test/java/com/wimp/app/specs/support/OrderingContext.java

[View file](After/src/test/java/com/wimp/app/specs/support/OrderingContext.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/OrderingContext.java#L1-L18)</sub>

```diff
@@ -0,0 +1,18 @@
+package com.wimp.app.specs.support;
+
+import io.cucumber.spring.ScenarioScope;
+import org.springframework.stereotype.Component;
+
+@Component
+@ScenarioScope
+public class OrderingContext {
+    private Integer currentOrderNo;
+
+    public Integer getCurrentOrderNo() {
+        return currentOrderNo;
+    }
+
+    public void setCurrentOrderNo(Integer currentOrderNo) {
+        this.currentOrderNo = currentOrderNo;
+    }
+}
```

### src/test/resources/com/wimp/app/specs/Ordering.feature

[View file](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L4)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L7)</sub>

```diff
@@ -4,6 +4,6 @@ Rule: A customer should receive a notification when their order is cancelled
 
 Scenario: The customer is notified about an order cancellation
   Given the customer "Rebecca" is authenticated
-  And the authenticated customer has placed the order #12342
+  And the authenticated customer has placed an order
   When the authenticated customer cancels the placed order
   Then the authenticated customer should receive a notification about the cancellation
```
