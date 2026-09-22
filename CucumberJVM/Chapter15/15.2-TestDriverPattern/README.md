# Pattern Differences: 15.2-TestDriverPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- ➕ Added [src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java](#srctestjavacomwimpappspecsdriversauthenticationapidriverjava)
- ➕ Added [src/test/java/com/wimp/app/specs/drivers/NotificationsApiDriver.java](#srctestjavacomwimpappspecsdriversnotificationsapidriverjava)
- ➕ Added [src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java](#srctestjavacomwimpappspecsdriversorderingapidriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsauthenticationstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/NotificationsStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsnotificationsstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/TestActionFailedException.java](#srctestjavacomwimpappspecssupporttestactionfailedexceptionjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java#L1-L47)</sub>

```diff
@@ -0,0 +1,47 @@
+package com.wimp.app.specs.drivers;
+
+import com.wimp.app.restapi.ErrorResponse;
+import com.wimp.app.restapi.LoginRequest;
+import com.wimp.app.restapi.LoginResponse;
+import com.wimp.app.specs.support.RestApiContext;
+import com.wimp.app.specs.support.TestActionFailedException;
+import org.springframework.http.HttpStatus;
+import org.springframework.http.MediaType;
+import org.springframework.stereotype.Component;
+import org.springframework.test.web.servlet.client.RestTestClient;
+import org.springframework.web.context.WebApplicationContext;
+
+import java.util.Objects;
+import java.util.Optional;
+
+@Component
+public class AuthenticationApiDriver {
+
+    private final RestApiContext restApiContext;
+    private final RestTestClient restTestClient;
+
+    public AuthenticationApiDriver(RestApiContext restApiContext, WebApplicationContext context) {
+        this.restApiContext = restApiContext;
+        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
+    }
+
+    public LoginResponse login(String customerName, String password) {
+        try {
+            var response = restTestClient.post().uri("/api/auth/login")
+                .accept(MediaType.APPLICATION_JSON)
+                .contentType(MediaType.APPLICATION_JSON)
+                .body(new LoginRequest(customerName, password))
+                .exchange();
+            if (response.returnResult().getStatus() != HttpStatus.OK) {
+                throw new RuntimeException("Request failed with status %s. Error message: %s".formatted(response.returnResult().getStatus(), Objects.requireNonNull(response.expectBody(ErrorResponse.class).returnResult().getResponseBody()).error()));
+            }
+            LoginResponse loginResponse =
+                Optional.ofNullable(response.expectBody(LoginResponse.class).returnResult().getResponseBody())
+                    .orElseThrow(() -> new RuntimeException("No result payload found"));
+            restApiContext.setBearerToken(loginResponse.token());
+            return loginResponse;
+        } catch (Exception ex) {
+            throw new TestActionFailedException(ex.getMessage());
+        }
+    }
+}
```

### src/test/java/com/wimp/app/specs/drivers/NotificationsApiDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/NotificationsApiDriver.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/NotificationsApiDriver.java#L1-L35)</sub>

```diff
@@ -0,0 +1,35 @@
+package com.wimp.app.specs.drivers;
+
+import com.wimp.app.models.Notification;
+import com.wimp.app.specs.support.TestActionFailedException;
+import org.springframework.core.ParameterizedTypeReference;
+import org.springframework.http.MediaType;
+import org.springframework.stereotype.Component;
+import org.springframework.test.web.servlet.client.RestTestClient;
+import org.springframework.web.context.WebApplicationContext;
+
+import java.util.List;
+import java.util.Optional;
+
+@Component
+public class NotificationsApiDriver {
+
+    private final RestTestClient restTestClient;
+
+    public NotificationsApiDriver(WebApplicationContext context) {
+        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
+    }
+
+    public List<Notification> getNotifications(String customerName) {
+        try {
+            var response = restTestClient.get().uri("/api/notifications/{customerName}", customerName)
+                .accept(MediaType.APPLICATION_JSON)
+                .exchange();
+            response.expectStatus().is2xxSuccessful();
+            return Optional.ofNullable(response.expectBody(new ParameterizedTypeReference<List<Notification>>() {}).returnResult().getResponseBody())
+                .orElseThrow(() -> new RuntimeException("No result payload found"));
+        } catch (Exception ex) {
+            throw new TestActionFailedException(ex.getMessage());
+        }
+    }
+}
```

### src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java#L1-L53)</sub>

```diff
@@ -0,0 +1,53 @@
+package com.wimp.app.specs.drivers;
+
+import com.wimp.app.models.Order;
+import com.wimp.app.restapi.PlaceOrderRequest;
+import com.wimp.app.specs.support.RestApiContext;
+import com.wimp.app.specs.support.TestActionFailedException;
+import org.springframework.http.HttpHeaders;
+import org.springframework.http.MediaType;
+import org.springframework.stereotype.Component;
+import org.springframework.test.web.servlet.client.RestTestClient;
+import org.springframework.web.context.WebApplicationContext;
+
+import java.util.Optional;
+
+@Component
+public class OrderingApiDriver {
+
+    private final RestTestClient restTestClient;
+    private final RestApiContext restApiContext;
+
+    public OrderingApiDriver(WebApplicationContext context, RestApiContext restApiContext) {
+        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
+        this.restApiContext = restApiContext;
+    }
+
+    public Order placeOrder(PlaceOrderRequest placeOrderRequest) {
+        try {
+            var response = restTestClient.post().uri("/api/orders")
+                .accept(MediaType.APPLICATION_JSON)
+                .contentType(MediaType.APPLICATION_JSON)
+                .header(HttpHeaders.AUTHORIZATION, "Bearer " + restApiContext.getBearerToken())
+                .body(placeOrderRequest)
+                .exchange();
+            response.expectStatus().is2xxSuccessful();
+            return Optional.ofNullable(response.expectBody(Order.class).returnResult().getResponseBody())
+                .orElseThrow(() -> new RuntimeException("No result payload found"));
+        } catch (Exception ex) {
+            throw new TestActionFailedException(ex.getMessage());
+        }
+    }
+
+    public void cancelOrder(int orderNo) {
+        try {
+            var response = restTestClient.delete().uri("/api/orders/{orderNo}", orderNo)
+                .accept(MediaType.APPLICATION_JSON)
+                .header(HttpHeaders.AUTHORIZATION, "Bearer " + restApiContext.getBearerToken())
+                .exchange();
+            response.expectStatus().is2xxSuccessful();
+        } catch (Exception ex) {
+            throw new TestActionFailedException(ex.getMessage());
+        }
+    }
+}
```

### src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java#L3-L41)</sub>

```diff
@@ -1,62 +1,43 @@
 package com.wimp.app.specs.stepdefinitions;
 
-import com.wimp.app.restapi.ErrorResponse;
-import com.wimp.app.restapi.LoginRequest;
-import com.wimp.app.restapi.LoginResponse;
+import com.wimp.app.specs.drivers.AuthenticationApiDriver;
 import com.wimp.app.specs.support.AuthenticationContext;
 import com.wimp.app.specs.support.DomainDefaults;
-import com.wimp.app.specs.support.RestApiContext;
+import com.wimp.app.specs.support.TestActionFailedException;
 import io.cucumber.java.en.*;
-import org.springframework.http.MediaType;
-import org.springframework.test.web.servlet.client.RestTestClient;
-import org.springframework.web.context.WebApplicationContext;
-
-import java.util.Optional;
 
 import static org.junit.jupiter.api.Assertions.assertNotNull;
 import static org.junit.jupiter.api.Assertions.assertTrue;
 
 public class AuthenticationStepDefinitions {
     private final AuthenticationContext authenticationContext;
-    private final RestApiContext restApiContext;
-    private final RestTestClient restTestClient;
-    private RestTestClient.ResponseSpec loginApiResponse;
+    private final AuthenticationApiDriver authenticationApiDriver;
+    private TestActionFailedException loginError;
 
-    public AuthenticationStepDefinitions(AuthenticationContext authenticationContext, RestApiContext restApiContext, WebApplicationContext context) {
+    public AuthenticationStepDefinitions(AuthenticationContext authenticationContext, AuthenticationApiDriver authenticationApiDriver) {
         this.authenticationContext = authenticationContext;
-        this.restApiContext = restApiContext;
-        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
+        this.authenticationApiDriver = authenticationApiDriver;
     }
 
     @Given("the customer is authenticated")
     public void theCustomerIsAuthenticated() throws Exception {
-        var response = restTestClient.post().uri("/api/auth/login")
-            .accept(MediaType.APPLICATION_JSON)
-            .contentType(MediaType.APPLICATION_JSON)
-            .body(new LoginRequest(DomainDefaults.CUSTOMER_NAME, DomainDefaults.PASSWORD))
-            .exchange();
-        response.expectStatus().is2xxSuccessful();
-        var loginResponse =
-            Optional.ofNullable(response.expectBody(LoginResponse.class).returnResult().getResponseBody())
-                .orElseThrow(() -> new RuntimeException("No result payload found"));
-        restApiContext.setBearerToken(loginResponse.token());
+        authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.PASSWORD);
         authenticationContext.setAuthenticatedCustomerName(DomainDefaults.CUSTOMER_NAME);
     }
 
     @When("the customer attempts to log in with a wrong password")
     public void theCustomerAttemptsToLogInWithAWrongPassword() throws Exception {
-        loginApiResponse = restTestClient.post().uri("/api/auth/login")
-            .accept(MediaType.APPLICATION_JSON)
-            .contentType(MediaType.APPLICATION_JSON)
-            .body(new LoginRequest(DomainDefaults.CUSTOMER_NAME, DomainDefaults.WRONG_PASSWORD))
-            .exchange();
+        try {
+            authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.WRONG_PASSWORD);
+            loginError = null;
+        } catch (TestActionFailedException ex) {
+            loginError = ex;
+        }
     }
 
     @Then("the login should fail with {string}")
     public void theLoginShouldFailWith(String expectedMessage) {
-        assertNotNull(loginApiResponse);
-        loginApiResponse.expectStatus().isUnauthorized();
-        var errorMessage = loginApiResponse.expectBody(ErrorResponse.class).returnResult().getResponseBody().error();
-        assertTrue(errorMessage.contains(expectedMessage), "Login should fail with the right error message (`%s`), but failed with `%s`".formatted(expectedMessage, errorMessage));
+        assertNotNull(loginError);
+        assertTrue(loginError.getMessage().contains(expectedMessage), "Login should fail with the right error message (`%s`), but failed with `%s`".formatted(expectedMessage, loginError.getMessage()));
     }
 }
```

### src/test/java/com/wimp/app/specs/stepdefinitions/NotificationsStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/NotificationsStepDefinitions.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/NotificationsStepDefinitions.java#L3-L23)</sub>

```diff
@@ -1,35 +1,26 @@
 package com.wimp.app.specs.stepdefinitions;
 
-import com.wimp.app.models.Notification;
+import com.wimp.app.specs.drivers.NotificationsApiDriver;
 import com.wimp.app.specs.support.AuthenticationContext;
 import io.cucumber.java.en.*;
-import org.springframework.core.ParameterizedTypeReference;
-import org.springframework.http.MediaType;
-import org.springframework.test.web.servlet.client.RestTestClient;
-import org.springframework.web.context.WebApplicationContext;
-
-import java.util.List;
 
 import static org.junit.jupiter.api.Assertions.assertNotNull;
 import static org.junit.jupiter.api.Assertions.assertTrue;
 
 public class NotificationsStepDefinitions {
     private final AuthenticationContext authContext;
-    private final RestTestClient restTestClient;
+    private final NotificationsApiDriver notificationsApiDriver;
 
-    public NotificationsStepDefinitions(AuthenticationContext authenticationContext, WebApplicationContext context) {
+    public NotificationsStepDefinitions(AuthenticationContext authenticationContext, NotificationsApiDriver notificationsApiDriver) {
         this.authContext = authenticationContext;
-        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
+        this.notificationsApiDriver = notificationsApiDriver;
     }
 
     @Then("they should receive a notification about the cancellation")
     public void theyShouldReceiveANotificationAboutTheCancellation() throws Exception {
         var customerName = authContext.getAuthenticatedCustomerNameVerified();
-        var response = restTestClient.get().uri("/api/notifications/{customerName}", customerName)
-            .accept(MediaType.APPLICATION_JSON)
-            .exchange();
-        response.expectStatus().is2xxSuccessful();
-        var notifications = response.expectBody(new ParameterizedTypeReference<List<Notification>>() {}).returnResult().getResponseBody();
+        var notifications = notificationsApiDriver.getNotifications(customerName);
+
         assertNotNull(notifications);
         assertTrue(notifications.stream().anyMatch(n -> n.message().contains("cancelled")),
             "Expected a cancellation notification but none was found.");
```

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L3-L28)</sub>

```diff
@@ -1,49 +1,30 @@
 package com.wimp.app.specs.stepdefinitions;
 
-import com.wimp.app.models.Order;
+import com.wimp.app.specs.drivers.OrderingApiDriver;
 import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
-import com.wimp.app.specs.support.RestApiContext;
 import io.cucumber.java.en.*;
-import org.springframework.http.HttpHeaders;
-import org.springframework.http.MediaType;
-import org.springframework.test.web.servlet.client.RestTestClient;
-import org.springframework.web.context.WebApplicationContext;
 
 import java.util.Optional;
 
 public class OrderingStepDefinitions {
-    private final RestApiContext restApiContext;
-    private final RestTestClient restTestClient;
+    private final OrderingApiDriver orderingApiDriver;
 
     private Integer placedOrderNo;
 
-    public OrderingStepDefinitions(RestApiContext restApiContext, WebApplicationContext context) {
-        this.restApiContext = restApiContext;
-        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
+    public OrderingStepDefinitions(OrderingApiDriver orderingApiDriver) {
+        this.orderingApiDriver = orderingApiDriver;
     }
 
     @Given("they have placed an order")
     public void theyHavePlacedAnOrder() throws Exception {
         var placeOrderRequest = new PlaceOrderRequestObjectMother().build();
-        var response = restTestClient.post().uri("/api/orders")
-            .accept(MediaType.APPLICATION_JSON)
-            .contentType(MediaType.APPLICATION_JSON)
-            .header(HttpHeaders.AUTHORIZATION, "Bearer " + restApiContext.getBearerToken())
-            .body(placeOrderRequest)
-            .exchange();
-        response.expectStatus().is2xxSuccessful();
-        var placedOrder = Optional.ofNullable(response.expectBody(Order.class).returnResult().getResponseBody())
-            .orElseThrow(() -> new RuntimeException("No result payload found"));
+        var placedOrder = orderingApiDriver.placeOrder(placeOrderRequest);
         placedOrderNo = placedOrder.getOrderNo();
     }
 
     @When("they cancel the placed order")
     public void theyCancelTheOrder() throws Exception {
         int orderNo = Optional.ofNullable(placedOrderNo).orElseThrow(() -> new RuntimeException("No placed order"));
-        var response = restTestClient.delete().uri("/api/orders/{orderNo}", orderNo)
-            .accept(MediaType.APPLICATION_JSON)
-            .header(HttpHeaders.AUTHORIZATION, "Bearer " + restApiContext.getBearerToken())
-            .exchange();
-        response.expectStatus().is2xxSuccessful();
+        orderingApiDriver.cancelOrder(orderNo);
     }
 }
```

### src/test/java/com/wimp/app/specs/support/TestActionFailedException.java

[View file](After/src/test/java/com/wimp/app/specs/support/TestActionFailedException.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/TestActionFailedException.java#L1-L7)</sub>

```diff
@@ -0,0 +1,7 @@
+package com.wimp.app.specs.support;
+
+public class TestActionFailedException extends RuntimeException {
+    public TestActionFailedException(String message) {
+        super(message);
+    }
+}
```
