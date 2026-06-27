# Pattern Differences: 15.4-TheTestActionPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Drivers/AuthenticationApiDriver.cs](#wimpspecsdriversauthenticationapidrivercs)
- 📝 Modified [WIMP.Specs/Drivers/NotificationsApiDriver.cs](#wimpspecsdriversnotificationsapidrivercs)
- 📝 Modified [WIMP.Specs/Drivers/OrderingApiDriver.cs](#wimpspecsdriversorderingapidrivercs)
- 📝 Modified [WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs](#wimpspecsstepdefinitionsauthenticationstepdefinitionscs)
- 📝 Modified [WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpspecsstepdefinitionsorderingstepdefinitionscs)
- ➕ Added [WIMP.Specs/Support/LambdaAction.cs](#wimpspecssupportlambdaactioncs)
- ➕ Added [WIMP.Specs/Support/TestAction.cs](#wimpspecssupporttestactioncs)

## Detailed Changes

### WIMP.Specs/Drivers/AuthenticationApiDriver.cs

[View file](After/WIMP.Specs/Drivers/AuthenticationApiDriver.cs#L1)

```diff
@@ -1,4 +1,3 @@
-using System.Diagnostics;
 using System.Net;
 
 using WIMP.App.RestApi;
```

<sub>[Jump to change](After/WIMP.Specs/Drivers/AuthenticationApiDriver.cs#L10-L24)</sub>

```diff
@@ -8,45 +7,19 @@ namespace WIMP.Specs.Drivers;
 
 public class AuthenticationApiDriver(RestApiContext restApiContext)
 {
-    public async Task<LoginResponse> PerformLogin(string customerName, string password)
-    {
-        Console.WriteLine("Executing Login...");
-        var stopwatch = Stopwatch.StartNew();
-        try
+    public TestAction<LoginResponse> Login(string customerName, string password) =>
+        new LambdaAction<LoginResponse>("Login", async () =>
         {
             var loginResponse = await restApiContext.ProcessRequest<LoginResponse>(
                 "Login", HttpMethod.Post, "/api/auth/login",
                 new LoginRequest(customerName, password));
-            Console.WriteLine(
-                $"Login executed successfully in {stopwatch.Elapsed}.");
-
             restApiContext.BearerToken = loginResponse.Token;
             return loginResponse;
-        }
-        catch (Exception ex)
-        {
-            Console.WriteLine($"Login failed: {ex.Message}");
-            throw;
-        }
-    }
+        });
 
-    public async Task PerformRegistration(string customerName, string email)
-    {
-        Console.WriteLine("Executing Register...");
-        var stopwatch = Stopwatch.StartNew();
-        try
-        {
+    public TestAction<VoidReturn> Register(string customerName, string email) =>
+        new LambdaAction("Register", async () =>
             await restApiContext.ProcessRequest<VoidReturn>(
                 "Register", HttpMethod.Post, "/api/auth/register",
-                new RegisterRequest(customerName, email), HttpStatusCode.Created);
-            Console.WriteLine(
-                $"Register executed successfully in {stopwatch.Elapsed}.");
-        }
-        catch (Exception ex)
-        {
-            Console.WriteLine($"Register failed: {ex.Message}");
-            throw;
-        }
-    }
-
+                new RegisterRequest(customerName, email), HttpStatusCode.Created));
 }
```

### WIMP.Specs/Drivers/NotificationsApiDriver.cs

[View file](After/WIMP.Specs/Drivers/NotificationsApiDriver.cs#L5)

<sub>[Jump to change](After/WIMP.Specs/Drivers/NotificationsApiDriver.cs#L8-L11)</sub>

```diff
@@ -5,9 +5,8 @@ namespace WIMP.Specs.Drivers;
 
 public class NotificationsApiDriver(RestApiContext restApiContext)
 {
-    public async Task<IReadOnlyCollection<Notification>> GetNotifications(string customerName)
-    {
-        return await restApiContext.GetRequest<Notification[]>(
-            $"/api/notifications/{customerName}");
-    }
+    public TestAction<IReadOnlyCollection<Notification>> GetNotifications(string customerName) =>
+        new LambdaAction<IReadOnlyCollection<Notification>>("Get notifications", async () =>
+            await restApiContext.GetRequest<Notification[]>(
+                $"/api/notifications/{customerName}"));
 }
```

### WIMP.Specs/Drivers/OrderingApiDriver.cs

[View file](After/WIMP.Specs/Drivers/OrderingApiDriver.cs#L8)

<sub>[Jump to change](After/WIMP.Specs/Drivers/OrderingApiDriver.cs#L11-L21)</sub>

```diff
@@ -8,17 +8,15 @@ namespace WIMP.Specs.Drivers;
 
 public class OrderingApiDriver(RestApiContext restApiContext)
 {
-    public async Task<Order> PerformPlaceOrder(PlaceOrderRequest placeOrderRequest)
-    {
-        return await restApiContext.ProcessRequest<Order>(
-            "Place order", HttpMethod.Post, "/api/orders",
-            placeOrderRequest, HttpStatusCode.Created);
-    }
+    public TestAction<Order> PlaceOrder(PlaceOrderRequest placeOrderRequest) =>
+        new LambdaAction<Order>("Place order", async () =>
+            await restApiContext.ProcessRequest<Order>(
+                "Place order", HttpMethod.Post, "/api/orders",
+                placeOrderRequest, HttpStatusCode.Created));
 
-    public async Task PerformCancelOrder(int orderNumber)
-    {
-        await restApiContext.ProcessRequest<VoidReturn>(
-            "Cancel order", HttpMethod.Delete, $"/api/orders/{orderNumber}",
-            successStatusCode: HttpStatusCode.NoContent);
-    }
+    public TestAction<VoidReturn> CancelOrder(int orderNumber) =>
+        new LambdaAction("Cancel order", async () =>
+            await restApiContext.ProcessRequest<VoidReturn>(
+                "Cancel order", HttpMethod.Delete, $"/api/orders/{orderNumber}",
+                successStatusCode: HttpStatusCode.NoContent));
 }
```

### WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs#L13)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs#L16)</sub>

```diff
@@ -13,7 +13,7 @@ public class AuthenticationStepDefinitions(AuthenticationContext authContext, Au
     [Given("the customer has authenticated")]
     public async Task GivenTheCustomerHasAuthenticated()
     {
-        await authApiDriver.PerformLogin(DomainDefaults.CustomerName, DomainDefaults.Password);
+        await authApiDriver.Login(DomainDefaults.CustomerName, DomainDefaults.Password).Execute();
         authContext.LoggedInCustomerName = DomainDefaults.CustomerName;
     }
 
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs#L25)</sub>

```diff
@@ -22,7 +22,7 @@ public class AuthenticationStepDefinitions(AuthenticationContext authContext, Au
     {
         try
         {
-            await authApiDriver.PerformLogin(DomainDefaults.CustomerName, DomainDefaults.WrongPassword);
+            await authApiDriver.Login(DomainDefaults.CustomerName, DomainDefaults.WrongPassword).Execute();
             loginError = null;
         }
         catch (WimpActionFailedException ex)
```

### WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L14)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L17)</sub>

```diff
@@ -14,7 +14,7 @@ public class OrderingStepDefinitions(AuthenticationContext authContext, Ordering
     public async Task GivenTheyHavePlacedAnOrder()
     {
         var placeOrderRequest = new PlaceOrderRequestObjectMother().Build();
-        var placedOrder = await orderingApiDriver.PerformPlaceOrder(placeOrderRequest);
+        var placedOrder = await orderingApiDriver.PlaceOrder(placeOrderRequest).Execute();
         placedOrderNo = placedOrder.OrderNo;
     }
 
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L25-L32)</sub>

```diff
@@ -22,14 +22,14 @@ public class OrderingStepDefinitions(AuthenticationContext authContext, Ordering
     public async Task WhenTheyCancelThePlacedOrder()
     {
         int orderNumber = placedOrderNo ?? throw new InvalidOperationException("No placed order");
-        await orderingApiDriver.PerformCancelOrder(orderNumber);
+        await orderingApiDriver.CancelOrder(orderNumber).Execute();
     }
 
     [Then("they should receive a notification about the cancellation")]
     public async Task ThenTheyShouldReceiveANotificationAboutTheCancellation()
     {
         string customerName = authContext.LoggedInCustomerName ?? throw new InvalidOperationException("No logged in customer name");
-        var notifications = await notificationsApiDriver.GetNotifications(customerName);
+        var notifications = await notificationsApiDriver.GetNotifications(customerName).Execute();
 
         Assert.IsNotNull(notifications);
         Assert.IsTrue(notifications.Any(n =>
```

### WIMP.Specs/Support/LambdaAction.cs

[View file](After/WIMP.Specs/Support/LambdaAction.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/LambdaAction.cs#L1-L38)</sub>

```diff
@@ -0,0 +1,38 @@
+namespace WIMP.Specs.Support;
+
+public class LambdaAction<TResult>(string testActionName, Func<Task<TResult>> action)
+    : TestAction<TResult>(testActionName)
+{
+    /// <summary>
+    /// Creates a lambda action with a synchronous action
+    /// </summary>
+    public LambdaAction(string testActionName, Func<TResult> action)
+        : this(testActionName, () => Task.FromResult(action()))
+    {
+    }
+
+    protected override async Task<TResult> DoExecute()
+    {
+        return await action();
+    }
+}
+
+/// <summary>
+/// Lambda action for void-return actions
+/// </summary>
+public class LambdaAction(string testActionName, Func<Task> action) :
+    LambdaAction<VoidReturn>(testActionName,
+        () => action().ContinueWith(_ => VoidReturn.Instance))
+{
+    /// <summary>
+    /// Creates a void-return lambda action with a synchronous action
+    /// </summary>
+    public LambdaAction(string testActionName, Action action)
+        : this(testActionName, () =>
+        {
+            action();
+            return Task.CompletedTask;
+        })
+    {
+    }
+}
```

### WIMP.Specs/Support/TestAction.cs

[View file](After/WIMP.Specs/Support/TestAction.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/TestAction.cs#L1-L27)</sub>

```diff
@@ -0,0 +1,27 @@
+using System.Diagnostics;
+
+namespace WIMP.Specs.Support;
+
+public abstract class TestAction<TResult>(string actionName)
+{
+    public string TestActionName => actionName;
+
+    protected abstract Task<TResult> DoExecute();
+
+    public async Task<TResult> Execute()
+    {
+        Console.WriteLine($"Executing {TestActionName}...");
+        var stopwatch = Stopwatch.StartNew();
+        try
+        {
+            var result = await DoExecute();
+            Console.WriteLine($"{TestActionName} executed successfully in {stopwatch.Elapsed}.");
+            return result;
+        }
+        catch (Exception ex)
+        {
+            Console.WriteLine($"{TestActionName} failed: {ex.Message}");
+            throw;
+        }
+    }
+}
```
