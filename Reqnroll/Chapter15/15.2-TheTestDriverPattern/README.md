# Pattern Differences: 15.2-TheTestDriverPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- ➕ Added [WIMP.Specs/Drivers/AuthenticationApiDriver.cs](#wimpspecsdriversauthenticationapidrivercs)
- ➕ Added [WIMP.Specs/Drivers/NotificationsApiDriver.cs](#wimpspecsdriversnotificationsapidrivercs)
- ➕ Added [WIMP.Specs/Drivers/OrderingApiDriver.cs](#wimpspecsdriversorderingapidrivercs)
- 📝 Modified [WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs](#wimpspecsstepdefinitionsauthenticationstepdefinitionscs)
- 📝 Modified [WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpspecsstepdefinitionsorderingstepdefinitionscs)
- ➕ Added [WIMP.Specs/Support/WimpActionFailedException.cs](#wimpspecssupportwimpactionfailedexceptioncs)

## Detailed Changes

### WIMP.Specs/Drivers/AuthenticationApiDriver.cs

[View file](After/WIMP.Specs/Drivers/AuthenticationApiDriver.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/AuthenticationApiDriver.cs#L1-L29)</sub>

```diff
@@ -0,0 +1,29 @@
+using System.Net;
+using System.Net.Http.Json;
+
+using WIMP.App.RestApi;
+using WIMP.Specs.Support;
+
+namespace WIMP.Specs.Drivers;
+
+public class AuthenticationApiDriver(AppHostingContext appHostingContext, RestApiContext restApiContext)
+{
+    public async Task PerformLogin(string customerName, string password)
+    {
+        var payload = new LoginRequest(customerName, password);
+
+        var response = await appHostingContext.AppHost.CreateClient()
+            .PostAsJsonAsync("/api/auth/login", payload);
+
+        if (response.StatusCode != HttpStatusCode.OK)
+        {
+            string errorMessage = await response.Content.ReadAsStringAsync();
+            throw new WimpActionFailedException(
+                $"Login failed with status code {response.StatusCode}. Error message: '{errorMessage}'");
+        }
+
+        var loginResponse = await response.Content.ReadFromJsonAsync<LoginResponse>()
+                    ?? throw new InvalidOperationException("No result payload found");
+        restApiContext.BearerToken = loginResponse.Token;
+    }
+}
```

### WIMP.Specs/Drivers/NotificationsApiDriver.cs

[View file](After/WIMP.Specs/Drivers/NotificationsApiDriver.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/NotificationsApiDriver.cs#L1-L17)</sub>

```diff
@@ -0,0 +1,17 @@
+using System.Net.Http.Json;
+
+using WIMP.App.Models;
+using WIMP.Specs.Support;
+
+namespace WIMP.Specs.Drivers;
+
+public class NotificationsApiDriver(AppHostingContext appHostingContext)
+{
+    public async Task<IReadOnlyCollection<Notification>> GetNotifications(string customerName)
+    {
+        return await appHostingContext.AppHost.CreateClient()
+            .GetFromJsonAsync<Notification[]>(
+                $"/api/notifications/{customerName}")
+               ?? throw new InvalidOperationException("No result payload found");
+    }
+}
```

### WIMP.Specs/Drivers/OrderingApiDriver.cs

[View file](After/WIMP.Specs/Drivers/OrderingApiDriver.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/OrderingApiDriver.cs#L1-L51)</sub>

```diff
@@ -0,0 +1,51 @@
+using System.Net;
+using System.Net.Http.Headers;
+using System.Net.Http.Json;
+
+using WIMP.App.Models;
+using WIMP.App.RestApi;
+using WIMP.Specs.Support;
+
+namespace WIMP.Specs.Drivers;
+
+public class OrderingApiDriver(AppHostingContext appHostingContext, RestApiContext restApiContext)
+{
+    public async Task<Order> PerformPlaceOrder(PlaceOrderRequest placeOrderRequest)
+    {
+        var request = new HttpRequestMessage(HttpMethod.Post, "/api/orders");
+        request.Headers.Authorization = new AuthenticationHeaderValue(
+            "Bearer", restApiContext.BearerToken);
+        request.Content = JsonContent.Create(placeOrderRequest);
+        var response = await appHostingContext.AppHost.CreateClient()
+            .SendAsync(request);
+
+        if (response.StatusCode != HttpStatusCode.Created)
+        {
+            string? errorMessage =
+                (await response.Content.ReadFromJsonAsync<ErrorResponse>())?.Error;
+            throw new WimpActionFailedException(
+                $"Place order failed with status code {response.StatusCode}. " +
+                $"Error message: '{errorMessage}'");
+        }
+
+        return await response.Content.ReadFromJsonAsync<Order>()
+               ?? throw new InvalidOperationException("No result payload found");
+    }
+    public async Task PerformCancelOrder(int orderNumber)
+    {
+        var request = new HttpRequestMessage(HttpMethod.Delete, $"/api/orders/{orderNumber}");
+        request.Headers.Authorization = new AuthenticationHeaderValue(
+            "Bearer", restApiContext.BearerToken);
+        var response = await appHostingContext.AppHost.CreateClient()
+            .SendAsync(request);
+
+        if (response.StatusCode != HttpStatusCode.NoContent)
+        {
+            string? errorMessage =
+                (await response.Content.ReadFromJsonAsync<ErrorResponse>())?.Error;
+            throw new WimpActionFailedException(
+                $"Place order failed with status code {response.StatusCode}. " +
+                $"Error message: '{errorMessage}'");
+        }
+    }
+}
```

### WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs#L3-L38)</sub>

```diff
@@ -1,51 +1,41 @@
-using System.Net;
-using System.Net.Http.Json;
-
 using Reqnroll;
 
-using WIMP.App.RestApi;
+using WIMP.Specs.Drivers;
 using WIMP.Specs.Support;
 
 namespace WIMP.Specs.StepDefinitions;
 
 [Binding]
-public class AuthenticationStepDefinitions(AuthenticationContext authContext, RestApiContext restApiContext, AppHostingContext appHostingContext)
+public class AuthenticationStepDefinitions(AuthenticationContext authContext, AuthenticationApiDriver authApiDriver)
 {
-    private HttpResponseMessage? loginApiResponse;
+    private WimpActionFailedException? loginError;
 
     [Given("the customer has authenticated")]
     public async Task GivenTheCustomerHasAuthenticated()
     {
-        var payload = new LoginRequest(
-            DomainDefaults.CustomerName,
-            DomainDefaults.Password);
-        var response = await appHostingContext.AppHost.CreateClient()
-            .PostAsJsonAsync("/api/auth/login", payload);
-
-        Assert.AreEqual(HttpStatusCode.OK, response.StatusCode);
-        var loginResponse = await response.Content.ReadFromJsonAsync<LoginResponse>()
-                            ?? throw new InvalidOperationException("No result payload found");
-        restApiContext.BearerToken = loginResponse.Token;
+        await authApiDriver.PerformLogin(DomainDefaults.CustomerName, DomainDefaults.Password);
         authContext.LoggedInCustomerName = DomainDefaults.CustomerName;
     }
 
     [When("the customer attempts to log in with a wrong password")]
     public async Task WhenTheCustomerAttemptsToLogInWithAWrongPassword()
     {
-        var payload = new LoginRequest(
-            DomainDefaults.CustomerName,
-            DomainDefaults.WrongPassword);
-        loginApiResponse = await appHostingContext.AppHost.CreateClient()
-            .PostAsJsonAsync("/api/auth/login", payload);
+        try
+        {
+            await authApiDriver.PerformLogin(DomainDefaults.CustomerName, DomainDefaults.WrongPassword);
+            loginError = null;
+        }
+        catch (WimpActionFailedException ex)
+        {
+            loginError = ex;
+        }
     }
 
     [Then("the login should fail with {string}")]
-    public async Task ThenTheLoginShouldFailWith(string expectedMessage)
+    public void ThenTheLoginShouldFailWith(string expectedMessage)
     {
-        Assert.IsNotNull(loginApiResponse);
-        Assert.AreNotEqual(HttpStatusCode.OK, loginApiResponse.StatusCode, "Login should fail");
-        string errorMessage = await loginApiResponse.Content.ReadAsStringAsync();
-        StringAssert.Contains(errorMessage, expectedMessage,
+        Assert.IsNotNull(loginError, "Login should fail");
+        StringAssert.Contains(loginError.Message, expectedMessage,
             "Login should fail with the right error message");
     }
 }
```

### WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L3-L9)</sub>

```diff
@@ -1,16 +1,12 @@
-using System.Net;
-using System.Net.Http.Headers;
-using System.Net.Http.Json;
-
 using Reqnroll;
 
-using WIMP.App.Models;
+using WIMP.Specs.Drivers;
 using WIMP.Specs.Support;
 
 namespace WIMP.Specs.StepDefinitions;
 
 [Binding]
-public class OrderingStepDefinitions(AuthenticationContext authContext, RestApiContext restApiContext, AppHostingContext appHostingContext)
+public class OrderingStepDefinitions(AuthenticationContext authContext, OrderingApiDriver orderingApiDriver, NotificationsApiDriver notificationsApiDriver)
 {
     private int? placedOrderNo;
 
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L17)</sub>

```diff
@@ -18,18 +14,7 @@ public class OrderingStepDefinitions(AuthenticationContext authContext, RestApiC
     public async Task GivenTheyHavePlacedAnOrder()
     {
         var placeOrderRequest = new PlaceOrderRequestObjectMother().Build();
-        var request = new HttpRequestMessage(HttpMethod.Post, "/api/orders");
-        request.Headers.Authorization = new AuthenticationHeaderValue(
-            "Bearer", restApiContext.BearerToken);
-        request.Content = JsonContent.Create(placeOrderRequest);
-        var response = await appHostingContext.AppHost.CreateClient()
-            .SendAsync(request);
-
-        Assert.AreEqual(HttpStatusCode.Created, response.StatusCode, $"Place order failed with status code {response.StatusCode}.");
-
-        var placedOrder = await response.Content.ReadFromJsonAsync<Order>()
-                    ?? throw new InvalidOperationException("No result payload found");
-
+        var placedOrder = await orderingApiDriver.PerformPlaceOrder(placeOrderRequest);
         placedOrderNo = placedOrder.OrderNo;
     }
 
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L25-L32)</sub>

```diff
@@ -37,22 +22,14 @@ public class OrderingStepDefinitions(AuthenticationContext authContext, RestApiC
     public async Task WhenTheyCancelThePlacedOrder()
     {
         int orderNumber = placedOrderNo ?? throw new InvalidOperationException("No placed order");
-        var request = new HttpRequestMessage(HttpMethod.Delete, $"/api/orders/{orderNumber}");
-        request.Headers.Authorization = new AuthenticationHeaderValue(
-            "Bearer", restApiContext.BearerToken);
-        var response = await appHostingContext.AppHost.CreateClient()
-            .SendAsync(request);
-
-        Assert.AreEqual(HttpStatusCode.NoContent, response.StatusCode, $"Place order failed with status code {response.StatusCode}.");
+        await orderingApiDriver.PerformCancelOrder(orderNumber);
     }
 
     [Then("they should receive a notification about the cancellation")]
     public async Task ThenTheyShouldReceiveANotificationAboutTheCancellation()
     {
         string customerName = authContext.LoggedInCustomerName ?? throw new InvalidOperationException("No logged in customer name");
-        var notifications = await appHostingContext.AppHost.CreateClient()
-            .GetFromJsonAsync<Notification[]>($"/api/notifications/{customerName}")
-            ?? throw new InvalidOperationException("No result payload found");
+        var notifications = await notificationsApiDriver.GetNotifications(customerName);
 
         Assert.IsNotNull(notifications);
         Assert.IsTrue(notifications.Any(n =>
```

### WIMP.Specs/Support/WimpActionFailedException.cs

[View file](After/WIMP.Specs/Support/WimpActionFailedException.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/WimpActionFailedException.cs#L1-L5)</sub>

```diff
@@ -0,0 +1,5 @@
+namespace WIMP.Specs.Support;
+
+public class WimpActionFailedException(string message) : Exception(message)
+{
+}
```
