# Pattern Differences: 13.2-ShareableScenarioContextPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsauthenticationstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/AuthenticationContext.java](#srctestjavacomwimpappspecssupportauthenticationcontextjava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Ordering.feature](#srctestresourcescomwimpappspecsorderingfeature)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java#L4-L17)</sub>

```diff
@@ -1,11 +1,19 @@
 package com.wimp.app.specs.stepdefinitions;
 
 import com.wimp.app.services.AuthenticationService;
+import com.wimp.app.specs.support.AuthenticationContext;
 import io.cucumber.java.en.*;
 
 public class AuthenticationStepDefinitions {
+    private final AuthenticationContext authenticationContext;
+
+    public AuthenticationStepDefinitions(AuthenticationContext authenticationContext) {
+        this.authenticationContext = authenticationContext;
+    }
+
     @Given("the customer {string} is authenticated")
     public void theCustomerIsAuthenticated(String customerName) {
         AuthenticationService.login(customerName);
+        authenticationContext.setAuthenticatedCustomerName(customerName);
     }
 }
```

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L3)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L6-L33)</sub>

```diff
@@ -3,28 +3,34 @@ package com.wimp.app.specs.stepdefinitions;
 import com.wimp.app.infrastructure.DataRepository;
 import com.wimp.app.services.NotificationService;
 import com.wimp.app.services.OrderService;
+import com.wimp.app.specs.support.AuthenticationContext;
 import io.cucumber.java.Before;
 import io.cucumber.java.en.*;
 
 import static org.junit.jupiter.api.Assertions.assertTrue;
 
 public class OrderingStepDefinitions {
+    private final AuthenticationContext authenticationContext;
     private int placedOrderNo;
 
-    @Given("the customer {string} has placed the order #{int}")
-    public void theCustomerHasPlacedTheOrder(String customerName, int orderNo) {
-        OrderService.placeOrder(customerName, orderNo, "Margherita");
+    public OrderingStepDefinitions(AuthenticationContext authenticationContext) {
+        this.authenticationContext = authenticationContext;
+    }
+
+    @Given("the authenticated customer has placed the order #{int}")
+    public void theAuthenticatedCustomerHasPlacedTheOrder(int orderNo) {
+        OrderService.placeOrder(authenticationContext.getAuthenticatedCustomerName(), orderNo, "Margherita");
         placedOrderNo = orderNo;
     }
 
-    @When("the customer {string} cancels the placed order")
-    public void theCustomerCancelsThePlacedOrder(String customerName) {
-        OrderService.cancelOrder(customerName, placedOrderNo);
+    @When("the authenticated customer cancels the placed order")
+    public void theAuthenticatedCustomerCancelsThePlacedOrder() {
+        OrderService.cancelOrder(authenticationContext.getAuthenticatedCustomerName(), placedOrderNo);
     }
 
-    @Then("the customer {string} should receive a notification about the cancellation")
-    public void theCustomerShouldReceiveANotification(String customerName) {
-        assertTrue(NotificationService.wasNotificationSent(customerName));
+    @Then("the authenticated customer should receive a notification about the cancellation")
+    public void theAuthenticatedCustomerShouldReceiveANotificationAboutTheCancellation() {
+        assertTrue(NotificationService.wasNotificationSent(authenticationContext.getAuthenticatedCustomerName()));
     }
 
     /**
```

### src/test/java/com/wimp/app/specs/support/AuthenticationContext.java

[View file](After/src/test/java/com/wimp/app/specs/support/AuthenticationContext.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/AuthenticationContext.java#L1-L18)</sub>

```diff
@@ -0,0 +1,18 @@
+package com.wimp.app.specs.support;
+
+import io.cucumber.spring.ScenarioScope;
+import org.springframework.stereotype.Component;
+
+@Component
+@ScenarioScope
+public class AuthenticationContext {
+    private String authenticatedCustomerName;
+
+    public String getAuthenticatedCustomerName() {
+        return authenticatedCustomerName;
+    }
+
+    public void setAuthenticatedCustomerName(String authenticatedCustomerName) {
+        this.authenticatedCustomerName = authenticatedCustomerName;
+    }
+}
```

### src/test/resources/com/wimp/app/specs/Ordering.feature

[View file](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L4)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Ordering.feature#L7-L9)</sub>

```diff
@@ -4,6 +4,6 @@ Rule: A customer should receive a notification when their order is cancelled
 
 Scenario: The customer is notified about an order cancellation
   Given the customer "Rebecca" is authenticated
-  And the customer "Rebecca" has placed the order #12342
-  When the customer "Rebecca" cancels the placed order
-  Then the customer "Rebecca" should receive a notification about the cancellation
+  And the authenticated customer has placed the order #12342
+  When the authenticated customer cancels the placed order
+  Then the authenticated customer should receive a notification about the cancellation
```
