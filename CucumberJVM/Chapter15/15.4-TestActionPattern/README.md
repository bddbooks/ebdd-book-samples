# Pattern Differences: 15.4-TestActionPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java](#srctestjavacomwimpappspecsdriversauthenticationapidriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/drivers/NotificationsApiDriver.java](#srctestjavacomwimpappspecsdriversnotificationsapidriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java](#srctestjavacomwimpappspecsdriversorderingapidriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsauthenticationstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/NotificationsStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsnotificationsstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/LambdaAction.java](#srctestjavacomwimpappspecssupportlambdaactionjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/TestAction.java](#srctestjavacomwimpappspecssupporttestactionjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java#L3)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java#L6-L9)</sub>

```diff
@@ -3,10 +3,10 @@ package com.wimp.app.specs.drivers;
 import com.wimp.app.restapi.LoginRequest;
 import com.wimp.app.restapi.LoginResponse;
 import com.wimp.app.restapi.RegisterRequest;
+import com.wimp.app.specs.support.LambdaAction;
 import com.wimp.app.specs.support.RestApiContext;
-import com.wimp.app.specs.support.TestActionFailedException;
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import com.wimp.app.specs.support.TestAction;
+import com.wimp.app.specs.support.VoidReturn;
 import org.springframework.stereotype.Component;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.service.annotation.HttpExchange;
```

```diff
@@ -14,9 +14,6 @@ import org.springframework.web.service.annotation.PostExchange;
 
 @Component
 public class AuthenticationApiDriver {
-
-    protected static final Logger log = LoggerFactory.getLogger(AuthenticationApiDriver.class);
-
     private final RestApiContext restApiContext;
     private final AuthenticationApiClient authenticationApiClient;
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java#L35-L45)</sub>

```diff
@@ -35,29 +32,16 @@ public class AuthenticationApiDriver {
         this.authenticationApiClient = authenticationApiClient;
     }
 
-    public LoginResponse login(String customerName, String password) {
-        log.info("Executing Login...");
-        long startTime = System.nanoTime();
-        try {
+    public TestAction<LoginResponse> login(String customerName, String password) {
+        return new LambdaAction<>("Login", "%s/%s".formatted(customerName, password), () -> {
             LoginResponse response = authenticationApiClient.login(new LoginRequest(customerName, password));
-            log.info("Login executed successfully in {}", (System.nanoTime() - startTime) / 1_000_000);
             restApiContext.setBearerToken(response.token());
             return response;
-        } catch (Exception ex) {
-            log.error("Login failed: {}", ex.getMessage());
-            throw new TestActionFailedException(ex.getMessage());
-        }
+        });
     }
 
-    public void register(String customerName, String email) {
-        log.info("Executing Register...");
-        long startTime = System.nanoTime();
-        try {
-            authenticationApiClient.register(new RegisterRequest(customerName, email));
-            log.info("Register executed successfully in {}", (System.nanoTime() - startTime) / 1_000_000);
-        } catch (Exception ex) {
-            log.error("Register failed: {}", ex.getMessage());
-            throw new TestActionFailedException(ex.getMessage());
-        }
+    public TestAction<VoidReturn> register(String customerName, String email) {
+        return new LambdaAction.Void("Register", "%s/%s".formatted(customerName, email), () ->
+            authenticationApiClient.register(new RegisterRequest(customerName, email)));
     }
 }
```

### src/test/java/com/wimp/app/specs/drivers/NotificationsApiDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/NotificationsApiDriver.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/NotificationsApiDriver.java#L4-L5)</sub>

```diff
@@ -1,9 +1,8 @@
 package com.wimp.app.specs.drivers;
 
 import com.wimp.app.models.Notification;
-import com.wimp.app.specs.support.TestActionFailedException;
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import com.wimp.app.specs.support.LambdaAction;
+import com.wimp.app.specs.support.TestAction;
 import org.springframework.stereotype.Component;
 import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.service.annotation.GetExchange;
```

```diff
@@ -13,9 +12,6 @@ import java.util.List;
 
 @Component
 public class NotificationsApiDriver {
-
-    protected static final Logger log = LoggerFactory.getLogger(NotificationsApiDriver.class);
-
     private final NotificationsApiClient notificationsApiClient;
 
     @HttpExchange("/api/notifications")
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/NotificationsApiDriver.java#L28-L30)</sub>

```diff
@@ -29,16 +25,8 @@ public class NotificationsApiDriver {
         this.notificationsApiClient = notificationsApiClient;
     }
 
-    public List<Notification> getNotifications(String customerName) {
-        log.info("Executing Get notifications...");
-        long startTime = System.nanoTime();
-        try {
-            var response = List.of(notificationsApiClient.getNotifications(customerName));
-            log.info("Get notifications executed successfully in {}", (System.nanoTime() - startTime) / 1_000_000);
-            return response;
-        } catch (Exception ex) {
-            log.error("Get notifications failed: {}", ex.getMessage());
-            throw new TestActionFailedException(ex.getMessage());
-        }
+    public TestAction<List<Notification>> getNotifications(String customerName) {
+        return new LambdaAction<>("Get notifications", customerName,
+            () -> List.of(notificationsApiClient.getNotifications(customerName)));
     }
 }
```

### src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java#L2)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java#L5-L7)</sub>

```diff
@@ -2,9 +2,9 @@ package com.wimp.app.specs.drivers;
 
 import com.wimp.app.models.Order;
 import com.wimp.app.restapi.PlaceOrderRequest;
-import com.wimp.app.specs.support.TestActionFailedException;
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import com.wimp.app.specs.support.LambdaAction;
+import com.wimp.app.specs.support.TestAction;
+import com.wimp.app.specs.support.VoidReturn;
 import org.springframework.stereotype.Component;
 import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.bind.annotation.RequestBody;
```

```diff
@@ -14,9 +14,6 @@ import org.springframework.web.service.annotation.PostExchange;
 
 @Component
 public class OrderingApiDriver {
-
-    protected static final Logger log = LoggerFactory.getLogger(OrderingApiDriver.class);
-
     private final OrderingApiClient orderingApiClient;
 
     @HttpExchange("/api/orders")
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java#L33-L39)</sub>

```diff
@@ -33,28 +30,12 @@ public class OrderingApiDriver {
         this.orderingApiClient = orderingApiClient;
     }
 
-    public Order placeOrder(PlaceOrderRequest placeOrderRequest) {
-        log.info("Executing Place order...");
-        long startTime = System.nanoTime();
-        try {
-            var response = orderingApiClient.placeOrder(placeOrderRequest);
-            log.info("Place order executed successfully in {}", (System.nanoTime() - startTime) / 1_000_000);
-            return response;
-        } catch (Exception ex) {
-            log.error("Place order failed: {}", ex.getMessage());
-            throw new TestActionFailedException(ex.getMessage());
-        }
+    public TestAction<Order> placeOrder(PlaceOrderRequest placeOrderRequest) {
+        return new LambdaAction<>("Place order", placeOrderRequest, () -> orderingApiClient.placeOrder(placeOrderRequest));
     }
 
-    public void cancelOrder(int orderNo) {
-        log.info("Executing Cancel order...");
-        long startTime = System.nanoTime();
-        try {
-            orderingApiClient.cancelOrder(orderNo);
-            log.info("Cancel order executed successfully in {}", (System.nanoTime() - startTime) / 1_000_000);
-        } catch (Exception ex) {
-            log.error("Cancel order failed: {}", ex.getMessage());
-            throw new TestActionFailedException(ex.getMessage());
-        }
+    public TestAction<VoidReturn> cancelOrder(int orderNo) {
+        return new LambdaAction.Void("Cancel order", orderNo, () ->
+            orderingApiClient.cancelOrder(orderNo));
     }
 }
```

### src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java#L21)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java#L24-L31)</sub>

```diff
@@ -21,14 +21,14 @@ public class AuthenticationStepDefinitions {
 
     @Given("the customer is authenticated")
     public void theCustomerIsAuthenticated() throws Exception {
-        authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.PASSWORD);
+        authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.PASSWORD).execute();
         authenticationContext.setAuthenticatedCustomerName(DomainDefaults.CUSTOMER_NAME);
     }
 
     @When("the customer attempts to log in with a wrong password")
     public void theCustomerAttemptsToLogInWithAWrongPassword() throws Exception {
         try {
-            authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.WRONG_PASSWORD);
+            authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.WRONG_PASSWORD).execute();
             loginError = null;
         } catch (TestActionFailedException ex) {
             loginError = ex;
```

### src/test/java/com/wimp/app/specs/stepdefinitions/NotificationsStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/NotificationsStepDefinitions.java#L19)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/NotificationsStepDefinitions.java#L22)</sub>

```diff
@@ -19,7 +19,7 @@ public class NotificationsStepDefinitions {
     @Then("they should receive a notification about the cancellation")
     public void theyShouldReceiveANotificationAboutTheCancellation() throws Exception {
         var customerName = authContext.getAuthenticatedCustomerNameVerified();
-        var notifications = notificationsApiDriver.getNotifications(customerName);
+        var notifications = notificationsApiDriver.getNotifications(customerName).execute();
 
         assertNotNull(notifications);
         assertTrue(notifications.stream().anyMatch(n -> n.message().contains("cancelled")),
```

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L18)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L21-L28)</sub>

```diff
@@ -18,13 +18,13 @@ public class OrderingStepDefinitions {
     @Given("they have placed an order")
     public void theyHavePlacedAnOrder() throws Exception {
         var placeOrderRequest = new PlaceOrderRequestObjectMother().build();
-        var placedOrder = orderingApiDriver.placeOrder(placeOrderRequest);
+        var placedOrder = orderingApiDriver.placeOrder(placeOrderRequest).execute();
         placedOrderNo = placedOrder.getOrderNo();
     }
 
     @When("they cancel the placed order")
     public void theyCancelTheOrder() throws Exception {
         int orderNo = Optional.ofNullable(placedOrderNo).orElseThrow(() -> new RuntimeException("No placed order"));
-        orderingApiDriver.cancelOrder(orderNo);
+        orderingApiDriver.cancelOrder(orderNo).execute();
     }
 }
```

### src/test/java/com/wimp/app/specs/support/LambdaAction.java

[View file](After/src/test/java/com/wimp/app/specs/support/LambdaAction.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/LambdaAction.java#L1-L50)</sub>

```diff
@@ -0,0 +1,50 @@
+package com.wimp.app.specs.support;
+
+public class LambdaAction<T> extends TestAction<T> {
+
+    /**
+     * Helper interface for specifying lambda actions that throw exceptions.
+     */
+    @FunctionalInterface
+    public interface ThrowingSupplier<T> {
+        T get() throws Exception;
+    }
+
+    private final ThrowingSupplier<T> action;
+
+    public LambdaAction(String testActionName, Object input, ThrowingSupplier<T> action) {
+        super(testActionName, input);
+        this.action = action;
+    }
+
+    public LambdaAction(String testActionName, ThrowingSupplier<T> action) {
+        this(testActionName, null, action);
+    }
+
+    @Override
+    public T doExecute() throws Exception {
+        return action.get();
+    }
+
+    /**
+     * Lambda action for void-returning actions.
+     */
+    public static class Void extends LambdaAction<VoidReturn> {
+
+        @FunctionalInterface
+        public interface ThrowingRunnable {
+            void run() throws Exception;
+        }
+
+        public Void(String testActionName, Object input, ThrowingRunnable action) {
+            super(testActionName, input, () -> {
+                action.run();
+                return VoidReturn.INSTANCE;
+            });
+        }
+
+        public Void(String testActionName, ThrowingRunnable action) {
+            this(testActionName, null, action);
+        }
+    }
+}
```

### src/test/java/com/wimp/app/specs/support/TestAction.java

[View file](After/src/test/java/com/wimp/app/specs/support/TestAction.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/TestAction.java#L1-L46)</sub>

```diff
@@ -0,0 +1,46 @@
+package com.wimp.app.specs.support;
+
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
+public abstract class TestAction<TResult> {
+
+    protected static final Logger log = LoggerFactory.getLogger(TestAction.class);
+
+    private final String testActionName;
+    private final Object input;
+
+    public String getTestActionName() {
+        return testActionName;
+    }
+
+    public Object getInput() {
+        return input;
+    }
+
+    public TestAction(String testActionName, Object input) {
+        this.testActionName = testActionName;
+        this.input = input;
+    }
+
+    public TestAction(String testActionName) {
+        this(testActionName, null);
+    }
+
+    abstract TResult doExecute() throws Exception;
+
+    public final TResult execute() throws Exception {
+        log.info("Executing {} with {}...", getTestActionName(), getInput());
+        long startTime = System.nanoTime();
+        try {
+            TResult result = doExecute();
+            log.info("{} executed successfully in {} ms with {}.", getTestActionName(), (System.nanoTime() - startTime) / 1_000_000, result);
+            return result;
+        } catch (Exception ex) {
+            log.error("{} failed: {}", getTestActionName(), ex.getMessage());
+            if (ex instanceof TestActionFailedException)
+                throw ex;
+            throw new TestActionFailedException("%s failed: %s".formatted(getTestActionName(), ex.getMessage()));
+        }
+    }
+}
```
