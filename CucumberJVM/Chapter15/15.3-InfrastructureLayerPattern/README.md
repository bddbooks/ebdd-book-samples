# Pattern Differences: 15.3-InfrastructureLayerPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/CucumberSpringConfiguration.java](#srctestjavacomwimpappcucumberspringconfigurationjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java](#srctestjavacomwimpappspecsdriversauthenticationapidriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/drivers/NotificationsApiDriver.java](#srctestjavacomwimpappspecsdriversnotificationsapidriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java](#srctestjavacomwimpappspecsdriversorderingapidriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/RestApiContext.java](#srctestjavacomwimpappspecssupportrestapicontextjava)

## Detailed Changes

### src/test/java/com/wimp/app/CucumberSpringConfiguration.java

[View file](After/src/test/java/com/wimp/app/CucumberSpringConfiguration.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/CucumberSpringConfiguration.java#L3-L13)</sub>

```diff
@@ -1,12 +1,16 @@
 package com.wimp.app;
 
+import com.wimp.app.specs.support.RestApiContext;
+
 import io.cucumber.spring.CucumberContextConfiguration;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.boot.test.context.TestConfiguration;
 import org.springframework.context.annotation.Bean;
 import org.springframework.test.context.ActiveProfiles;
 import org.springframework.test.web.servlet.MockMvc;
+import org.springframework.test.web.servlet.client.MockMvcClientHttpRequestFactory;
 import org.springframework.test.web.servlet.setup.MockMvcBuilders;
+import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
 import org.springframework.web.context.WebApplicationContext;
 import org.springframework.web.service.registry.ImportHttpServices;
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/CucumberSpringConfiguration.java#L41-L53)</sub>

```diff
@@ -34,5 +38,18 @@ public class CucumberSpringConfiguration {
         public MockMvc mockMvc(WebApplicationContext webApplicationContext) {
             return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
         }
+
+        /**
+         * Enable the HTTP Service Clients to use in-process REST API calls (the
+         * same infrastructure that supports RestTestClient as well).
+         */
+        @Bean
+        public RestClientHttpServiceGroupConfigurer specsClientHttpServiceGroupConfigurer(MockMvc mockMvc, RestApiContext restApiContext) {
+            return groups -> groups.filterByName("specs")
+                .forEachClient((_, clientBuilder) -> {
+                    clientBuilder.requestFactory(new MockMvcClientHttpRequestFactory(mockMvc));
+                    RestApiContext.configureRestApiCall(restApiContext, clientBuilder);
+                });
+        }
     }
 }
```

### src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java#L8-L33)</sub>

```diff
@@ -1,45 +1,36 @@
 package com.wimp.app.specs.drivers;
 
-import com.wimp.app.restapi.ErrorResponse;
 import com.wimp.app.restapi.LoginRequest;
 import com.wimp.app.restapi.LoginResponse;
 import com.wimp.app.specs.support.RestApiContext;
 import com.wimp.app.specs.support.TestActionFailedException;
-import org.springframework.http.HttpStatus;
-import org.springframework.http.MediaType;
 import org.springframework.stereotype.Component;
-import org.springframework.test.web.servlet.client.RestTestClient;
-import org.springframework.web.context.WebApplicationContext;
-
-import java.util.Objects;
-import java.util.Optional;
+import org.springframework.web.bind.annotation.RequestBody;
+import org.springframework.web.service.annotation.HttpExchange;
+import org.springframework.web.service.annotation.PostExchange;
 
 @Component
 public class AuthenticationApiDriver {
 
     private final RestApiContext restApiContext;
-    private final RestTestClient restTestClient;
+    private final AuthenticationApiClient authenticationApiClient;
+
+    @HttpExchange("/api/auth")
+    public interface AuthenticationApiClient {
+        @PostExchange("login")
+        LoginResponse login(@RequestBody LoginRequest request);
+    }
 
-    public AuthenticationApiDriver(RestApiContext restApiContext, WebApplicationContext context) {
+    public AuthenticationApiDriver(RestApiContext restApiContext, AuthenticationApiClient authenticationApiClient) {
         this.restApiContext = restApiContext;
-        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
+        this.authenticationApiClient = authenticationApiClient;
     }
 
     public LoginResponse login(String customerName, String password) {
         try {
-            var response = restTestClient.post().uri("/api/auth/login")
-                .accept(MediaType.APPLICATION_JSON)
-                .contentType(MediaType.APPLICATION_JSON)
-                .body(new LoginRequest(customerName, password))
-                .exchange();
-            if (response.returnResult().getStatus() != HttpStatus.OK) {
-                throw new RuntimeException("Request failed with status %s. Error message: %s".formatted(response.returnResult().getStatus(), Objects.requireNonNull(response.expectBody(ErrorResponse.class).returnResult().getResponseBody()).error()));
-            }
-            LoginResponse loginResponse =
-                Optional.ofNullable(response.expectBody(LoginResponse.class).returnResult().getResponseBody())
-                    .orElseThrow(() -> new RuntimeException("No result payload found"));
-            restApiContext.setBearerToken(loginResponse.token());
-            return loginResponse;
+            LoginResponse response = authenticationApiClient.login(new LoginRequest(customerName, password));
+            restApiContext.setBearerToken(response.token());
+            return response;
         } catch (Exception ex) {
             throw new TestActionFailedException(ex.getMessage());
         }
```

### src/test/java/com/wimp/app/specs/drivers/NotificationsApiDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/NotificationsApiDriver.java#L2)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/NotificationsApiDriver.java#L6-L29)</sub>

```diff
@@ -2,32 +2,31 @@ package com.wimp.app.specs.drivers;
 
 import com.wimp.app.models.Notification;
 import com.wimp.app.specs.support.TestActionFailedException;
-import org.springframework.core.ParameterizedTypeReference;
-import org.springframework.http.MediaType;
 import org.springframework.stereotype.Component;
-import org.springframework.test.web.servlet.client.RestTestClient;
-import org.springframework.web.context.WebApplicationContext;
+import org.springframework.web.bind.annotation.PathVariable;
+import org.springframework.web.service.annotation.GetExchange;
+import org.springframework.web.service.annotation.HttpExchange;
 
 import java.util.List;
-import java.util.Optional;
 
 @Component
 public class NotificationsApiDriver {
 
-    private final RestTestClient restTestClient;
+    private final NotificationsApiClient notificationsApiClient;
 
-    public NotificationsApiDriver(WebApplicationContext context) {
-        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
+    @HttpExchange("/api/notifications")
+    public interface NotificationsApiClient {
+        @GetExchange("/{customerName}")
+        Notification[] getNotifications(@PathVariable String customerName);
+    }
+
+    public NotificationsApiDriver(NotificationsApiClient notificationsApiClient) {
+        this.notificationsApiClient = notificationsApiClient;
     }
 
     public List<Notification> getNotifications(String customerName) {
         try {
-            var response = restTestClient.get().uri("/api/notifications/{customerName}", customerName)
-                .accept(MediaType.APPLICATION_JSON)
-                .exchange();
-            response.expectStatus().is2xxSuccessful();
-            return Optional.ofNullable(response.expectBody(new ParameterizedTypeReference<List<Notification>>() {}).returnResult().getResponseBody())
-                .orElseThrow(() -> new RuntimeException("No result payload found"));
+            return List.of(notificationsApiClient.getNotifications(customerName));
         } catch (Exception ex) {
             throw new TestActionFailedException(ex.getMessage());
         }
```

### src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java#L2)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java#L7-L33)</sub>

```diff
@@ -2,38 +2,35 @@ package com.wimp.app.specs.drivers;
 
 import com.wimp.app.models.Order;
 import com.wimp.app.restapi.PlaceOrderRequest;
-import com.wimp.app.specs.support.RestApiContext;
 import com.wimp.app.specs.support.TestActionFailedException;
-import org.springframework.http.HttpHeaders;
-import org.springframework.http.MediaType;
 import org.springframework.stereotype.Component;
-import org.springframework.test.web.servlet.client.RestTestClient;
-import org.springframework.web.context.WebApplicationContext;
-
-import java.util.Optional;
+import org.springframework.web.bind.annotation.PathVariable;
+import org.springframework.web.bind.annotation.RequestBody;
+import org.springframework.web.service.annotation.DeleteExchange;
+import org.springframework.web.service.annotation.HttpExchange;
+import org.springframework.web.service.annotation.PostExchange;
 
 @Component
 public class OrderingApiDriver {
 
-    private final RestTestClient restTestClient;
-    private final RestApiContext restApiContext;
+    private final OrderingApiClient orderingApiClient;
+
+    @HttpExchange("/api/orders")
+    public interface OrderingApiClient {
+        @PostExchange
+        Order placeOrder(@RequestBody PlaceOrderRequest request);
+
+        @DeleteExchange("/{orderNo}")
+        void cancelOrder(@PathVariable int orderNo);
+    }
 
-    public OrderingApiDriver(WebApplicationContext context, RestApiContext restApiContext) {
-        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
-        this.restApiContext = restApiContext;
+    public OrderingApiDriver(OrderingApiClient orderingApiClient) {
+        this.orderingApiClient = orderingApiClient;
     }
 
     public Order placeOrder(PlaceOrderRequest placeOrderRequest) {
         try {
-            var response = restTestClient.post().uri("/api/orders")
-                .accept(MediaType.APPLICATION_JSON)
-                .contentType(MediaType.APPLICATION_JSON)
-                .header(HttpHeaders.AUTHORIZATION, "Bearer " + restApiContext.getBearerToken())
-                .body(placeOrderRequest)
-                .exchange();
-            response.expectStatus().is2xxSuccessful();
-            return Optional.ofNullable(response.expectBody(Order.class).returnResult().getResponseBody())
-                .orElseThrow(() -> new RuntimeException("No result payload found"));
+            return orderingApiClient.placeOrder(placeOrderRequest);
         } catch (Exception ex) {
             throw new TestActionFailedException(ex.getMessage());
         }
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/OrderingApiDriver.java#L41)</sub>

```diff
@@ -41,11 +38,7 @@ public class OrderingApiDriver {
 
     public void cancelOrder(int orderNo) {
         try {
-            var response = restTestClient.delete().uri("/api/orders/{orderNo}", orderNo)
-                .accept(MediaType.APPLICATION_JSON)
-                .header(HttpHeaders.AUTHORIZATION, "Bearer " + restApiContext.getBearerToken())
-                .exchange();
-            response.expectStatus().is2xxSuccessful();
+            orderingApiClient.cancelOrder(orderNo);
         } catch (Exception ex) {
             throw new TestActionFailedException(ex.getMessage());
         }
```

### src/test/java/com/wimp/app/specs/support/RestApiContext.java

[View file](After/src/test/java/com/wimp/app/specs/support/RestApiContext.java#L17)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/RestApiContext.java#L20-L61)</sub>

```diff
@@ -17,13 +17,46 @@ import java.nio.charset.StandardCharsets;
 @Component
 @ScenarioScope
 public class RestApiContext {
+    protected static final Logger log = LoggerFactory.getLogger(RestApiContext.class);
     private String bearerToken;
 
-    public String getBearerToken() {
-        return bearerToken;
-    }
-
     public void setBearerToken(String bearerToken) {
         this.bearerToken = bearerToken;
     }
+
+    // This method must stay public, otherwise it won't be proxied for scenario scope.
+    public String getAuthorizationHeader() {
+        if (bearerToken != null && !bearerToken.isBlank()) {
+            return "Bearer " + bearerToken;
+        }
+        return null;
+    }
+
+    public static void configureRestApiCall(RestApiContext restApiContext, RestClient.Builder clientBuilder) {
+        // include Authorization header of the current scenario execution if available
+        clientBuilder.requestInitializer(request -> {
+            String authorizationHeader = restApiContext.getAuthorizationHeader();
+            if (authorizationHeader != null) {
+                request.getHeaders().set(HttpHeaders.AUTHORIZATION, authorizationHeader);
+            }
+        });
+        // add interceptor to perform custom logging of the execution
+        clientBuilder.requestInterceptor((request, body, execution) -> {
+            log.debug("REST API request: {} {}, Headers: {}, Content: {}", request.getMethod(), request.getURI(), request.getHeaders(), peekRequestBodyForLogging(body));
+            ClientHttpResponse response = execution.execute(request, body);
+            log.debug("REST API response: {}, Headers: {}, Content: {}", response.getStatusCode(), response.getHeaders(), peekResponseBodyForLogging(response));
+            return response;
+        });
+    }
+
+    private static @NonNull String peekRequestBodyForLogging(byte[] body) throws IOException {
+        return StreamUtils.copyToString(new ByteArrayInputStream(body), StandardCharsets.UTF_8);
+    }
+
+    private static @NonNull String peekResponseBodyForLogging(ClientHttpResponse response) throws IOException {
+        var bodyStream = response.getBody();
+        var content = StreamUtils.copyToString(bodyStream, StandardCharsets.UTF_8);
+        bodyStream.reset();
+        return content;
+    }
 }
```
