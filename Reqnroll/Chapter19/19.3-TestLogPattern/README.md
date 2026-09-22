# Pattern Differences: 19.3-TestLogPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Drivers/AuthenticationApiDriver.cs](#wimpspecsdriversauthenticationapidrivercs)
- 📝 Modified [WIMP.Specs/Drivers/OrderingApiDriver.cs](#wimpspecsdriversorderingapidrivercs)
- 📝 Modified [WIMP.Specs/Features/Authentication.feature](#wimpspecsfeaturesauthenticationfeature)
- 📝 Modified [WIMP.Specs/Support/LambdaAction.cs](#wimpspecssupportlambdaactioncs)
- ➕ Added [WIMP.Specs/Support/Logging/ReqnrollLogger.cs](#wimpspecssupportloggingreqnrollloggercs)
- ➕ Added [WIMP.Specs/Support/Logging/ReqnrollLoggerHooks.cs](#wimpspecssupportloggingreqnrollloggerhookscs)
- ➕ Added [WIMP.Specs/Support/Logging/ReqnrollLoggerProvider.cs](#wimpspecssupportloggingreqnrollloggerprovidercs)
- 📝 Modified [WIMP.Specs/Support/RestApiContext.cs](#wimpspecssupportrestapicontextcs)
- 📝 Modified [WIMP.Specs/Support/TestAction.cs](#wimpspecssupporttestactioncs)

## Detailed Changes

### WIMP.Specs/Drivers/AuthenticationApiDriver.cs

[View file](After/WIMP.Specs/Drivers/AuthenticationApiDriver.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/AuthenticationApiDriver.cs#L1-L11)</sub>

```diff
@@ -1,12 +1,14 @@
+using Microsoft.Extensions.Logging;
+
 using WIMP.App.RestApi;
 using WIMP.Specs.Support;
 
 namespace WIMP.Specs.Drivers;
 
-public class AuthenticationApiDriver(RestApiContext restApiContext)
+public class AuthenticationApiDriver(ILoggerFactory loggerFactory, RestApiContext restApiContext)
 {
     public TestAction<LoginResponse> Login(string customerName, string password) =>
-        new LambdaAction<LoginResponse>("Login", (customerName, password), async () =>
+        new LambdaAction<LoginResponse>(loggerFactory, "Login", (customerName, password), async () =>
         {
             var loginResponse = await restApiContext.ProcessRequest<LoginResponse>(
                 "Login", HttpMethod.Post, "/api/auth/login",
```

### WIMP.Specs/Drivers/OrderingApiDriver.cs

[View file](After/WIMP.Specs/Drivers/OrderingApiDriver.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/OrderingApiDriver.cs#L3-L14)</sub>

```diff
@@ -1,15 +1,17 @@
 using System.Net;
 
+using Microsoft.Extensions.Logging;
+
 using WIMP.App.Models;
 using WIMP.App.RestApi;
 using WIMP.Specs.Support;
 
 namespace WIMP.Specs.Drivers;
 
-public class OrderingApiDriver(RestApiContext restApiContext)
+public class OrderingApiDriver(ILoggerFactory loggerFactory, RestApiContext restApiContext)
 {
     public TestAction<Order> PlaceOrder(PlaceOrderRequest placeOrderRequest) =>
-        new LambdaAction<Order>("Place order", placeOrderRequest, async () =>
+        new LambdaAction<Order>(loggerFactory, "Place order", placeOrderRequest, async () =>
             await restApiContext.ProcessRequest<Order>(
                 "Place order", HttpMethod.Post, "/api/orders",
                 placeOrderRequest, HttpStatusCode.Created));
```

### WIMP.Specs/Features/Authentication.feature

[View file](After/WIMP.Specs/Features/Authentication.feature#L2)

<sub>[Jump to change](After/WIMP.Specs/Features/Authentication.feature#L5)</sub>

```diff
@@ -2,6 +2,7 @@
 
 Rule: Customer needs valid password for login
 
+  @log:debug
   Scenario: A registered customer logs in successfully
     When the customer attempts to log in with valid password
     Then they should be authenticated
```

### WIMP.Specs/Support/LambdaAction.cs

[View file](After/WIMP.Specs/Support/LambdaAction.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/LambdaAction.cs#L1-L33)</sub>

```diff
@@ -1,33 +1,36 @@
+using Microsoft.Extensions.Logging;
+
 namespace WIMP.Specs.Support;
 
 public class LambdaAction<TResult>(
+    ILoggerFactory loggerFactory,
     string testActionName,
     object? input,
     Func<Task<TResult>> action)
-    : TestAction<TResult>(testActionName, input)
+    : TestAction<TResult>(loggerFactory, testActionName, input)
 {
 
     /// <summary>
     /// Creates a lambda action without input
     /// </summary>
-    public LambdaAction(string testActionName, Func<Task<TResult>> action)
-        : this(testActionName, null, action)
+    public LambdaAction(ILoggerFactory loggerFactory, string testActionName, Func<Task<TResult>> action)
+        : this(loggerFactory, testActionName, null, action)
     {
     }
 
     /// <summary>
     /// Creates a lambda action with a synchronous action
     /// </summary>
-    public LambdaAction(string testActionName, object? input, Func<TResult> action)
-        : this(testActionName, input, () => Task.FromResult(action()))
+    public LambdaAction(ILoggerFactory loggerFactory, string testActionName, object? input, Func<TResult> action)
+        : this(loggerFactory, testActionName, input, () => Task.FromResult(action()))
     {
     }
 
     /// <summary>
     /// Creates a lambda action with a synchronous action, without input
     /// </summary>
-    public LambdaAction(string testActionName, Func<TResult> action)
-        : this(testActionName, null, () => Task.FromResult(action()))
+    public LambdaAction(ILoggerFactory loggerFactory, string testActionName, Func<TResult> action)
+        : this(loggerFactory, testActionName, null, () => Task.FromResult(action()))
     {
     }
 
```

<sub>[Jump to change](After/WIMP.Specs/Support/LambdaAction.cs#L46-L47)</sub>

```diff
@@ -40,8 +43,8 @@ public class LambdaAction<TResult>(
 /// <summary>
 /// Lambda action for void-return actions
 /// </summary>
-public class LambdaAction(string testActionName, object? input, Func<Task> action) :
-    LambdaAction<VoidReturn>(testActionName, input, async () =>
+public class LambdaAction(ILoggerFactory loggerFactory, string testActionName, object? input, Func<Task> action) :
+    LambdaAction<VoidReturn>(loggerFactory, testActionName, input, async () =>
     {
         await action();
         return VoidReturn.Instance;
```

<sub>[Jump to change](After/WIMP.Specs/Support/LambdaAction.cs#L56-L65)</sub>

```diff
@@ -50,16 +53,16 @@ public class LambdaAction(string testActionName, object? input, Func<Task> actio
     /// <summary>
     /// Creates a void-return lambda action without input
     /// </summary>
-    public LambdaAction(string testActionName, Func<Task> action)
-        : this(testActionName, null, action)
+    public LambdaAction(ILoggerFactory loggerFactory, string testActionName, Func<Task> action)
+        : this(loggerFactory, testActionName, null, action)
     {
     }
 
     /// <summary>
     /// Creates a void-return lambda action with a synchronous action
     /// </summary>
-    public LambdaAction(string testActionName, object? input, Action action)
-        : this(testActionName, input, () =>
+    public LambdaAction(ILoggerFactory loggerFactory, string testActionName, object? input, Action action)
+        : this(loggerFactory, testActionName, input, () =>
         {
             action();
             return Task.CompletedTask;
```

<sub>[Jump to change](After/WIMP.Specs/Support/LambdaAction.cs#L76-L77)</sub>

```diff
@@ -70,8 +73,8 @@ public class LambdaAction(string testActionName, object? input, Func<Task> actio
     /// <summary>
     /// Creates a void-return lambda action with a synchronous action, without input
     /// </summary>
-    public LambdaAction(string testActionName, Action action)
-        : this(testActionName, null, () =>
+    public LambdaAction(ILoggerFactory loggerFactory, string testActionName, Action action)
+        : this(loggerFactory, testActionName, null, () =>
         {
             action();
             return Task.CompletedTask;
```

### WIMP.Specs/Support/Logging/ReqnrollLogger.cs

[View file](After/WIMP.Specs/Support/Logging/ReqnrollLogger.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/Logging/ReqnrollLogger.cs#L1-L71)</sub>

```diff
@@ -0,0 +1,71 @@
+using Microsoft.Extensions.Logging;
+
+using Reqnroll;
+
+namespace WIMP.Specs.Support.Logging;
+
+/// <summary>
+/// An <see cref="ILogger"/> implementation that forwards log entries to Reqnroll output helper.
+/// </summary>
+internal class ReqnrollLogger(
+    string categoryName,
+    IExternalScopeProvider scopeProvider,
+    IReqnrollOutputHelper outputHelper) : ILogger
+{
+    protected string CategoryName => categoryName;
+    protected virtual string DisplayPrefix => "";
+    protected virtual LogLevel MinimumLevel => LogLevel.Trace;
+
+    public bool IsEnabled(LogLevel logLevel) => logLevel != LogLevel.None && logLevel >= MinimumLevel;
+
+    public IDisposable BeginScope<TState>(TState state) where TState : notnull =>
+        scopeProvider.Push(state);
+
+    public void Log<TState>(
+        LogLevel logLevel,
+        EventId eventId,
+        TState state,
+        Exception? exception,
+        Func<TState, Exception?, string> formatter)
+    {
+        if (!IsEnabled(logLevel))
+        {
+            return;
+        }
+
+        string message = formatter(state, exception);
+        if (string.IsNullOrWhiteSpace(message) && exception is null)
+        {
+            return;
+        }
+
+        var scopes = new List<string>();
+        scopeProvider.ForEachScope(
+            (scope, list) =>
+            {
+                if (scope is not null)
+                {
+                    list.Add(scope.ToString()!);
+                }
+            },
+            scopes);
+
+        string scopeSuffix = scopes.Count == 0
+            ? string.Empty
+            : $" => {string.Join(" => ", scopes)}";
+
+        string indent = new string(' ', scopes.Count * 2 + 2);
+        message = message.Replace("\n", "\n" + indent);
+
+        string displayCategoryName = CategoryName.Split('.').Last();
+        string displayLogLevel = logLevel == LogLevel.Information ? "Info" : logLevel.ToString();
+
+        outputHelper.WriteLine(
+            $"{indent}{DateTimeOffset.Now:HH:mm:ss} {DisplayPrefix}{displayLogLevel}: {displayCategoryName}[{eventId}]{scopeSuffix}: {message}");
+
+        if (exception is not null)
+        {
+            outputHelper.WriteLine(exception.ToString());
+        }
+    }
+}
```

### WIMP.Specs/Support/Logging/ReqnrollLoggerHooks.cs

[View file](After/WIMP.Specs/Support/Logging/ReqnrollLoggerHooks.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/Logging/ReqnrollLoggerHooks.cs#L1-L28)</sub>

```diff
@@ -0,0 +1,28 @@
+using Microsoft.Extensions.Logging;
+
+using Reqnroll;
+
+namespace WIMP.Specs.Support.Logging;
+
+[Binding]
+public class ReqnrollLoggerHooks(IScenarioContext scenarioContext, ReqnrollLoggerProvider reqnrollLoggerProvider)
+{
+    private const LogLevel DefaultMinimumLogLevel = LogLevel.Information;
+
+    [BeforeScenario(Order = -1)]
+    public void SetupLoggerFactory()
+    {
+        const string tagPrefix = "log:";
+        string? logLevelFromTag = scenarioContext.ScenarioInfo.CombinedTags
+            .FirstOrDefault(t => t.StartsWith(tagPrefix))?
+            .Substring(tagPrefix.Length);
+        var logLevel = logLevelFromTag != null ? Enum.Parse<LogLevel>(logLevelFromTag, true) : DefaultMinimumLogLevel;
+
+        var loggerFactory = LoggerFactory.Create(builder =>
+        {
+            builder.SetMinimumLevel(logLevel);
+            builder.AddProvider(reqnrollLoggerProvider);
+        });
+        scenarioContext.ScenarioContainer.RegisterInstanceAs(loggerFactory, dispose: true);
+    }
+}
```

### WIMP.Specs/Support/Logging/ReqnrollLoggerProvider.cs

[View file](After/WIMP.Specs/Support/Logging/ReqnrollLoggerProvider.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/Logging/ReqnrollLoggerProvider.cs#L1-L54)</sub>

```diff
@@ -0,0 +1,54 @@
+using System.Collections.Concurrent;
+
+using Microsoft.Extensions.Logging;
+
+using Reqnroll;
+
+namespace WIMP.Specs.Support.Logging;
+
+/// <summary>
+/// An <see cref="ILoggerProvider"/> implementation that creates <see cref="ILogger"/> loggers,
+/// that forward the log messages to Reqnroll output helper.
+/// </summary>
+public sealed class ReqnrollLoggerProvider(IReqnrollOutputHelper outputHelper) : ILoggerProvider
+{
+    private readonly IExternalScopeProvider scopeProvider = new ScenarioScopeProvider();
+
+    /// <summary>
+    /// The custom <see cref="IExternalScopeProvider"/> implementation is needed for using
+    /// scopes as test hierarchy.
+    /// </summary>
+    private class ScenarioScopeProvider : IExternalScopeProvider
+    {
+        private readonly ConcurrentStack<object?> scopeStack = new();
+
+        private class StackPopper(ScenarioScopeProvider scopeProvider) : IDisposable
+        {
+            public void Dispose()
+            {
+                scopeProvider.scopeStack.TryPop(out _);
+            }
+        }
+
+        public void ForEachScope<TState>(Action<object?, TState> callback, TState state)
+        {
+            foreach (object? scope in scopeStack.ToArray())
+            {
+                callback(scope, state);
+            }
+        }
+
+        public IDisposable Push(object? state)
+        {
+            scopeStack.Push(state);
+            return new StackPopper(this);
+        }
+    }
+
+    public ILogger CreateLogger(string categoryName) =>
+        new ReqnrollLogger(categoryName, scopeProvider, outputHelper);
+
+    public void Dispose()
+    {
+    }
+}
```

### WIMP.Specs/Support/RestApiContext.cs

[View file](After/WIMP.Specs/Support/RestApiContext.cs#L3)

<sub>[Jump to change](After/WIMP.Specs/Support/RestApiContext.cs#L6-L7)</sub>

```diff
@@ -3,6 +3,8 @@ using System.Net.Http.Headers;
 using System.Net.Http.Json;
 using System.Text;
 
+using Microsoft.Extensions.Logging;
+
 using WIMP.App.RestApi;
 
 namespace WIMP.Specs.Support;
```

<sub>[Jump to change](After/WIMP.Specs/Support/RestApiContext.cs#L17-L19)</sub>

```diff
@@ -12,8 +14,9 @@ public record VoidReturn
     public static readonly VoidReturn Instance = new();
 }
 
-public class RestApiContext(AppHostingContext appHostingContext)
+public class RestApiContext(AppHostingContext appHostingContext, ILoggerFactory loggerFactory)
 {
+    private ILogger Logger => loggerFactory.CreateLogger(GetType());
     public string? BearerToken { get; set; }
 
     public async Task<TResult> GetRequest<TResult>(
```

<sub>[Jump to change](After/WIMP.Specs/Support/RestApiContext.cs#L64-L69)</sub>

```diff
@@ -58,8 +61,12 @@ public class RestApiContext(AppHostingContext appHostingContext)
             request.Content = JsonContent.Create(payload);
         }
 
+        Logger.LogDebug("REST API request: {Request}", await GetRequestLogContentForLogging(request));
         var httpClient = appHostingContext.AppHost.CreateClient();
-        return await httpClient.SendAsync(request);
+        var response = await httpClient.SendAsync(request);
+        Logger.LogDebug("REST API response: {Response}", await GetResponseLogContentForLogging(response));
+
+        return response;
     }
 
     private async Task<string> GetRequestLogContentForLogging(HttpRequestMessage request)
```

### WIMP.Specs/Support/TestAction.cs

[View file](After/WIMP.Specs/Support/TestAction.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/TestAction.cs#L3-L9)</sub>

```diff
@@ -1,9 +1,12 @@
 using System.Diagnostics;
 
+using Microsoft.Extensions.Logging;
+
 namespace WIMP.Specs.Support;
 
-public abstract class TestAction<TResult>(string actionName, object? input = null)
+public abstract class TestAction<TResult>(ILoggerFactory loggerFactory, string actionName, object? input = null)
 {
+    protected ILogger Logger => loggerFactory.CreateLogger(GetType());
     public string TestActionName => actionName;
     public object? Input { get; } = input;
 
```

<sub>[Jump to change](After/WIMP.Specs/Support/TestAction.cs#L17-L31)</sub>

```diff
@@ -11,17 +14,21 @@ public abstract class TestAction<TResult>(string actionName, object? input = nul
 
     public async Task<TResult> Execute()
     {
-        Console.WriteLine($"Executing {TestActionName} with {Input}...");
+        Logger.LogInformation("Executing {TestActionName} with {Input}...", TestActionName, Input);
         var stopwatch = Stopwatch.StartNew();
         try
         {
-            var result = await DoExecute();
-            Console.WriteLine($"{TestActionName} executed successfully in {stopwatch.Elapsed}.");
+            TResult result;
+            using (Logger.BeginScope($"TestAction.{TestActionName}"))
+            {
+                result = await DoExecute();
+            }
+            Logger.LogInformation("{TestActionName} executed successfully in {Duration} with {Result}.", TestActionName, stopwatch.Elapsed, result);
             return result;
         }
         catch (Exception ex)
         {
-            Console.WriteLine($"{TestActionName} failed: {ex.Message}");
+            Logger.LogError("{TestActionName} failed: {Message}", TestActionName, ex.Message);
             throw;
         }
     }
```
