# Pattern Differences: 15.3-TheInfrastructureLayerPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Drivers/AuthenticationApiDriver.cs](#wimpspecsdriversauthenticationapidrivercs)
- 📝 Modified [WIMP.Specs/Drivers/NotificationsApiDriver.cs](#wimpspecsdriversnotificationsapidrivercs)
- 📝 Modified [WIMP.Specs/Drivers/OrderingApiDriver.cs](#wimpspecsdriversorderingapidrivercs)
- 📝 Modified [WIMP.Specs/Support/RestApiContext.cs](#wimpspecssupportrestapicontextcs)

## Detailed Changes

### WIMP.Specs/Drivers/AuthenticationApiDriver.cs

[View file](After/WIMP.Specs/Drivers/AuthenticationApiDriver.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/AuthenticationApiDriver.cs#L6-L14)</sub>

```diff
@@ -1,29 +1,17 @@
-using System.Net;
-using System.Net.Http.Json;
-
 using WIMP.App.RestApi;
 using WIMP.Specs.Support;
 
 namespace WIMP.Specs.Drivers;
 
-public class AuthenticationApiDriver(AppHostingContext appHostingContext, RestApiContext restApiContext)
+public class AuthenticationApiDriver(RestApiContext restApiContext)
 {
     public async Task PerformLogin(string customerName, string password)
     {
-        var payload = new LoginRequest(customerName, password);
-
-        var response = await appHostingContext.AppHost.CreateClient()
-            .PostAsJsonAsync("/api/auth/login", payload);
-
-        if (response.StatusCode != HttpStatusCode.OK)
-        {
-            string errorMessage = await response.Content.ReadAsStringAsync();
-            throw new WimpActionFailedException(
-                $"Login failed with status code {response.StatusCode}. Error message: '{errorMessage}'");
-        }
-
-        var loginResponse = await response.Content.ReadFromJsonAsync<LoginResponse>()
-                    ?? throw new InvalidOperationException("No result payload found");
+        var loginResponse = await restApiContext.ProcessRequest<LoginResponse>(
+            "Login",
+            HttpMethod.Post,
+            "/api/auth/login",
+            new LoginRequest(customerName, password));
         restApiContext.BearerToken = loginResponse.Token;
     }
 }
```

### WIMP.Specs/Drivers/NotificationsApiDriver.cs

[View file](After/WIMP.Specs/Drivers/NotificationsApiDriver.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/NotificationsApiDriver.cs#L6-L11)</sub>

```diff
@@ -1,17 +1,13 @@
-using System.Net.Http.Json;
-
 using WIMP.App.Models;
 using WIMP.Specs.Support;
 
 namespace WIMP.Specs.Drivers;
 
-public class NotificationsApiDriver(AppHostingContext appHostingContext)
+public class NotificationsApiDriver(RestApiContext restApiContext)
 {
     public async Task<IReadOnlyCollection<Notification>> GetNotifications(string customerName)
     {
-        return await appHostingContext.AppHost.CreateClient()
-            .GetFromJsonAsync<Notification[]>(
-                $"/api/notifications/{customerName}")
-               ?? throw new InvalidOperationException("No result payload found");
+        return await restApiContext.GetRequest<Notification[]>(
+            $"/api/notifications/{customerName}");
     }
 }
```

### WIMP.Specs/Drivers/OrderingApiDriver.cs

[View file](After/WIMP.Specs/Drivers/OrderingApiDriver.cs#L1)

```diff
@@ -1,6 +1,4 @@
 using System.Net;
-using System.Net.Http.Headers;
-using System.Net.Http.Json;
 
 using WIMP.App.Models;
 using WIMP.App.RestApi;
```

<sub>[Jump to change](After/WIMP.Specs/Drivers/OrderingApiDriver.cs#L9-L27)</sub>

```diff
@@ -8,44 +6,24 @@ using WIMP.Specs.Support;
 
 namespace WIMP.Specs.Drivers;
 
-public class OrderingApiDriver(AppHostingContext appHostingContext, RestApiContext restApiContext)
+public class OrderingApiDriver(RestApiContext restApiContext)
 {
     public async Task<Order> PerformPlaceOrder(PlaceOrderRequest placeOrderRequest)
     {
-        var request = new HttpRequestMessage(HttpMethod.Post, "/api/orders");
-        request.Headers.Authorization = new AuthenticationHeaderValue(
-            "Bearer", restApiContext.BearerToken);
-        request.Content = JsonContent.Create(placeOrderRequest);
-        var response = await appHostingContext.AppHost.CreateClient()
-            .SendAsync(request);
-
-        if (response.StatusCode != HttpStatusCode.Created)
-        {
-            string? errorMessage =
-                (await response.Content.ReadFromJsonAsync<ErrorResponse>())?.Error;
-            throw new WimpActionFailedException(
-                $"Place order failed with status code {response.StatusCode}. " +
-                $"Error message: '{errorMessage}'");
-        }
-
-        return await response.Content.ReadFromJsonAsync<Order>()
-               ?? throw new InvalidOperationException("No result payload found");
+        return await restApiContext.ProcessRequest<Order>(
+            "Place order",
+            HttpMethod.Post,
+            "/api/orders",
+            placeOrderRequest,
+            HttpStatusCode.Created);
     }
+
     public async Task PerformCancelOrder(int orderNumber)
     {
-        var request = new HttpRequestMessage(HttpMethod.Delete, $"/api/orders/{orderNumber}");
-        request.Headers.Authorization = new AuthenticationHeaderValue(
-            "Bearer", restApiContext.BearerToken);
-        var response = await appHostingContext.AppHost.CreateClient()
-            .SendAsync(request);
-
-        if (response.StatusCode != HttpStatusCode.NoContent)
-        {
-            string? errorMessage =
-                (await response.Content.ReadFromJsonAsync<ErrorResponse>())?.Error;
-            throw new WimpActionFailedException(
-                $"Place order failed with status code {response.StatusCode}. " +
-                $"Error message: '{errorMessage}'");
-        }
+        await restApiContext.ProcessRequest<VoidReturn>(
+            "Cancel order",
+            HttpMethod.Delete,
+            $"/api/orders/{orderNumber}",
+            successStatusCode: HttpStatusCode.NoContent);
     }
 }
```

### WIMP.Specs/Support/RestApiContext.cs

[View file](After/WIMP.Specs/Support/RestApiContext.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/RestApiContext.cs#L1-L72)</sub>

```diff
@@ -1,6 +1,73 @@
+using System.Net;
+using System.Net.Http.Headers;
+using System.Net.Http.Json;
+
+using WIMP.App.RestApi;
+
 namespace WIMP.Specs.Support;
 
-public class RestApiContext
+public record VoidReturn;
+
+public class RestApiContext(AppHostingContext appHostingContext)
 {
     public string? BearerToken { get; set; }
+
+    public async Task<TResult> GetRequest<TResult>(
+        string path, object? payload = null)
+    {
+        var response = await SendRequest(HttpMethod.Get, path, payload);
+        response.EnsureSuccessStatusCode();
+        return await ReadResult<TResult>(response);
+    }
+
+    public async Task<TResult> ProcessRequest<TResult>(
+        string actionName, HttpMethod method, string path,
+        object? payload = null,
+        HttpStatusCode successStatusCode = HttpStatusCode.OK)
+    {
+        var response = await SendRequest(method, path, payload);
+        if (response.StatusCode != successStatusCode)
+        {
+            string errorMessage = await ReadErrorMessage(response);
+            throw new WimpActionFailedException(
+                $"{actionName} failed with status code {response.StatusCode}. " +
+                $"Error message: '{errorMessage}'");
+        }
+
+        return typeof(TResult) == typeof(VoidReturn)
+            ? default!
+            : await ReadResult<TResult>(response);
+    }
+
+    private async Task<HttpResponseMessage> SendRequest(
+        HttpMethod method, string path, object? payload = null)
+    {
+        var request = new HttpRequestMessage(method, path);
+        if (!string.IsNullOrEmpty(BearerToken))
+        {
+            request.Headers.Authorization =
+                new AuthenticationHeaderValue("Bearer", BearerToken);
+        }
+
+        if (payload != null)
+        {
+            request.Content = JsonContent.Create(payload);
+        }
+
+        var httpClient = appHostingContext.AppHost.CreateClient();
+        return await httpClient.SendAsync(request);
+    }
+
+    private async Task<TResult> ReadResult<TResult>(HttpResponseMessage response)
+    {
+        return await response.Content.ReadFromJsonAsync<TResult>()
+            ?? throw new InvalidOperationException("No result payload found");
+    }
+
+    private async Task<string> ReadErrorMessage(HttpResponseMessage response)
+    {
+        string? errorMessage =
+            (await response.Content.ReadFromJsonAsync<ErrorResponse>())?.Error;
+        return string.IsNullOrEmpty(errorMessage) ? "n/a" : errorMessage;
+    }
 }
```
