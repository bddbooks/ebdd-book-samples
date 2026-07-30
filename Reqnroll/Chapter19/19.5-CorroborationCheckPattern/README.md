# Pattern Differences: 19.5-CorroborationCheckPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

In order to trigger order rejections that do not modify the test result, but represent an issue that should be alerted for, you need to introduce a warning log to `OrderService.PlaceOrder()` method.

Uncommenting the `<DefineConstants>` setting in `WIMP.App.csproj` activates such an issue without the need to change the `OrderService` class.


## Summary of Changes

- ➕ Added [WIMP.Specs/Support/CorroborationCheckException.cs](#wimpspecssupportcorroborationcheckexceptioncs)
- 📝 Modified [WIMP.Specs/Support/Hooks.cs](#wimpspecssupporthookscs)
- 📝 Modified [WIMP.Specs/Support/Logging/AppLogContext.cs](#wimpspecssupportloggingapplogcontextcs)

## Detailed Changes

### WIMP.Specs/Support/CorroborationCheckException.cs

[View file](After/WIMP.Specs/Support/CorroborationCheckException.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/CorroborationCheckException.cs#L1-L3)</sub>

```diff
@@ -0,0 +1,3 @@
+namespace WIMP.Specs.Support;
+
+public class CorroborationCheckException(string message) : Exception(message);
```

### WIMP.Specs/Support/Hooks.cs

[View file](After/WIMP.Specs/Support/Hooks.cs#L5)

<sub>[Jump to change](After/WIMP.Specs/Support/Hooks.cs#L8-L19)</sub>

```diff
@@ -5,8 +5,18 @@ using WIMP.Specs.Support.Logging;
 namespace WIMP.Specs.Support;
 
 [Binding]
-public class Hooks(AppHostingContext appHostingContext, AppLogContext appLogContext, ReqnrollLoggerProvider reqnrollLoggerProvider)
+public class Hooks(AppHostingContext appHostingContext, AppLogContext appLogContext, ReqnrollLoggerProvider reqnrollLoggerProvider,
+    IScenarioContext scenarioContext)
 {
+    [AfterScenario]
+    public void CheckAppHealth()
+    {
+        if (scenarioContext.ScenarioExecutionStatus == ScenarioExecutionStatus.OK)
+        {
+            appLogContext.CheckAppHealth();
+        }
+    }
+
     [BeforeScenario]
     public void CreateAppHost()
     {
```

### WIMP.Specs/Support/Logging/AppLogContext.cs

[View file](After/WIMP.Specs/Support/Logging/AppLogContext.cs#L11)

<sub>[Jump to change](After/WIMP.Specs/Support/Logging/AppLogContext.cs#L14-L34)</sub>

```diff
@@ -11,12 +11,27 @@ namespace WIMP.Specs.Support.Logging;
 public class AppLogContext
 {
     private readonly ConcurrentQueue<string> logMessages = new();
+    private readonly ConcurrentQueue<string> healthIssues = new();
 
     public IReadOnlyCollection<string> LogMessages => logMessages;
 
     public void AddLogMessage(LogLevel logLevel, string logMessage)
     {
         logMessages.Enqueue(logMessage);
+        if (logLevel >= LogLevel.Warning)
+        {
+            healthIssues.Enqueue(logMessage);
+        }
+    }
+
+    public void CheckAppHealth()
+    {
+        if (healthIssues.Any())
+        {
+            string issueText = string.Join(Environment.NewLine, healthIssues);
+            throw new CorroborationCheckException(
+                $"The application log contains warnings or errors:{Environment.NewLine}{issueText}");
+        }
     }
 
     public void SaveToFile(string outputPath)
```
