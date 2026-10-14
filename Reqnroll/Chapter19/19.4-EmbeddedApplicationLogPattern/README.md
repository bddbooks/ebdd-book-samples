# Pattern Differences: 19.4-EmbeddedApplicationLogPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Support/Hooks.cs](#wimpspecssupporthookscs)
- ➕ Added [WIMP.Specs/Support/Logging/EmbeddedAppHostLogger.cs](#wimpspecssupportloggingembeddedapphostloggercs)
- ➕ Added [WIMP.Specs/Support/Logging/EmbeddedAppHostLoggerProvider.cs](#wimpspecssupportloggingembeddedapphostloggerprovidercs)
- 📝 Modified [WIMP.Specs/Support/Logging/TestLoggerProvider.cs](#wimpspecssupportloggingtestloggerprovidercs)
- 📝 Modified [WIMP.Specs/Support/WimpAppHost.cs](#wimpspecssupportwimpapphostcs)

## Detailed Changes

### WIMP.Specs/Support/Hooks.cs

[View file](After/WIMP.Specs/Support/Hooks.cs#L5)

<sub>[Jump to change](After/WIMP.Specs/Support/Hooks.cs#L8-L13)</sub>

```diff
@@ -5,12 +5,12 @@ using WIMP.Specs.Support.Logging;
 namespace WIMP.Specs.Support;
 
 [Binding]
-public class Hooks(AppHostingContext appHostingContext, AppLogContext appLogContext)
+public class Hooks(AppHostingContext appHostingContext, AppLogContext appLogContext, TestLoggerProvider testLoggerProvider)
 {
     [BeforeScenario]
     public void CreateAppHost()
     {
-        appHostingContext.AppHost = new WimpAppHost(appLogContext);
+        appHostingContext.AppHost = new WimpAppHost(appLogContext, testLoggerProvider);
     }
 
     [AfterScenario]
```

### WIMP.Specs/Support/Logging/EmbeddedAppHostLogger.cs

[View file](After/WIMP.Specs/Support/Logging/EmbeddedAppHostLogger.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/Logging/EmbeddedAppHostLogger.cs#L1-L28)</sub>

```diff
@@ -0,0 +1,28 @@
+using Microsoft.Extensions.Logging;
+
+using Reqnroll;
+
+namespace WIMP.Specs.Support.Logging;
+
+/// <summary>
+/// An <see cref="ILogger"/> implementation that forwards log entries to Reqnroll output helper
+/// via the <see cref="TestLogger"/> base class.
+/// </summary>
+internal class EmbeddedAppHostLogger(string categoryName, IExternalScopeProvider scopeProvider, IReqnrollOutputHelper outputHelper, IScenarioContext scenarioContext) :
+    TestLogger(categoryName, scopeProvider, outputHelper)
+{
+    private const LogLevel MinimumAppLogLevel = LogLevel.Warning;
+    private const LogLevel MinimumAppLogLevelForActions = LogLevel.Information;
+
+    /// <summary>
+    /// Gets whether the logger is for a WIMP application log or for ASP.NET infrastructure log
+    /// </summary>
+    private bool IsWimpCategory => CategoryName.StartsWith("WIMP");
+
+    protected override LogLevel MinimumLevel =>
+        scenarioContext.CurrentScenarioBlock == ScenarioBlock.When || IsWimpCategory
+            ? MinimumAppLogLevelForActions
+            : MinimumAppLogLevel;
+
+    protected override string DisplayPrefix => IsWimpCategory ? "WIMP/" : "ASP.NET/";
+}
```

### WIMP.Specs/Support/Logging/EmbeddedAppHostLoggerProvider.cs

[View file](After/WIMP.Specs/Support/Logging/EmbeddedAppHostLoggerProvider.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/Logging/EmbeddedAppHostLoggerProvider.cs#L1-L17)</sub>

```diff
@@ -0,0 +1,17 @@
+using Microsoft.Extensions.Logging;
+
+namespace WIMP.Specs.Support.Logging;
+
+/// <summary>
+/// An <see cref="ILoggerProvider"/> implementation that creates <see cref="ILogger"/> loggers,
+/// that forwards log entries to Reqnroll output helper via the <see cref="TestLogger"/> base class.
+/// </summary>
+public sealed class EmbeddedAppHostLoggerProvider(TestLoggerProvider testLoggerProvider) : ILoggerProvider
+{
+    public ILogger CreateLogger(string categoryName) =>
+        testLoggerProvider.CreateEmbeddedAppHostLogger(categoryName);
+
+    public void Dispose()
+    {
+    }
+}
```

### WIMP.Specs/Support/Logging/TestLoggerProvider.cs

[View file](After/WIMP.Specs/Support/Logging/TestLoggerProvider.cs#L10)

<sub>[Jump to change](After/WIMP.Specs/Support/Logging/TestLoggerProvider.cs#L13)</sub>

```diff
@@ -10,7 +10,7 @@ namespace WIMP.Specs.Support.Logging;
 /// An <see cref="ILoggerProvider"/> implementation that creates <see cref="ILogger"/> loggers,
 /// that forward the log messages to Reqnroll output helper.
 /// </summary>
-public sealed class TestLoggerProvider(IReqnrollOutputHelper outputHelper) : ILoggerProvider
+public sealed class TestLoggerProvider(IReqnrollOutputHelper outputHelper, IScenarioContext scenarioContext) : ILoggerProvider
 {
     private readonly IExternalScopeProvider scopeProvider = new ScenarioScopeProvider();
 
```

<sub>[Jump to change](After/WIMP.Specs/Support/Logging/TestLoggerProvider.cs#L51-L53)</sub>

```diff
@@ -48,6 +48,9 @@ public sealed class TestLoggerProvider(IReqnrollOutputHelper outputHelper) : ILo
     public ILogger CreateLogger(string categoryName) =>
         new TestLogger(categoryName, scopeProvider, outputHelper);
 
+    public ILogger CreateEmbeddedAppHostLogger(string categoryName) =>
+        new EmbeddedAppHostLogger(categoryName, scopeProvider, outputHelper, scenarioContext);
+
     public void Dispose()
     {
     }
```

### WIMP.Specs/Support/WimpAppHost.cs

[View file](After/WIMP.Specs/Support/WimpAppHost.cs#L9)

<sub>[Jump to change](After/WIMP.Specs/Support/WimpAppHost.cs#L12)</sub>

```diff
@@ -9,7 +9,7 @@ using WIMP.Specs.Support.Logging;
 
 namespace WIMP.Specs.Support;
 
-public class WimpAppHost(AppLogContext appLogContext) : WebApplicationFactory<Program>
+public class WimpAppHost(AppLogContext appLogContext, TestLoggerProvider testLoggerProvider) : WebApplicationFactory<Program>
 {
     protected override void ConfigureWebHost(IWebHostBuilder builder)
     {
```

<sub>[Jump to change](After/WIMP.Specs/Support/WimpAppHost.cs#L25-L26)</sub>

```diff
@@ -22,6 +22,8 @@ public class WimpAppHost(AppLogContext appLogContext) : WebApplicationFactory<Pr
             loggingBuilder.Services.TryAddEnumerable(ServiceDescriptor.Singleton<ILoggerProvider, DebugLoggerProvider>(_ => new DebugLoggerProvider()));
             // log app log entries to AppLogContext
             loggingBuilder.Services.TryAddEnumerable(ServiceDescriptor.Singleton<ILoggerProvider, AppLogContextLoggerProvider>(_ => new AppLogContextLoggerProvider(appLogContext)));
+            // embed app log to the test log
+            loggingBuilder.Services.TryAddEnumerable(ServiceDescriptor.Singleton<ILoggerProvider, EmbeddedAppHostLoggerProvider>(_ => new EmbeddedAppHostLoggerProvider(testLoggerProvider)));
         });
     }
 }
```
