# Pattern Differences: 19.2-SupplementaryAttachmentPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

In order to see the attached application logs in this sample, you need to introduce a bug to the `OrderService.TakeNextOrder()` method (for example change `OrderBy` to `OrderByDescending`).

Uncommenting the `<DefineConstants>` setting in `WIMP.App.csproj` activates this bug without the need to change the `OrderService` class.


## Summary of Changes

- 📝 Modified [WIMP.Specs/Support/Hooks.cs](#wimpspecssupporthookscs)

## Detailed Changes

### WIMP.Specs/Support/Hooks.cs

[View file](After/WIMP.Specs/Support/Hooks.cs#L5)

<sub>[Jump to change](After/WIMP.Specs/Support/Hooks.cs#L8-L21)</sub>

```diff
@@ -5,8 +5,20 @@ using WIMP.Specs.Support.Logging;
 namespace WIMP.Specs.Support;
 
 [Binding]
-public class Hooks(AppHostingContext appHostingContext, AppLogContext appLogContext)
+public class Hooks(AppHostingContext appHostingContext, AppLogContext appLogContext,
+    IScenarioContext scenarioContext, IReqnrollOutputHelper outputHelper)
 {
+    [AfterScenario(Order = 0)]
+    public void SaveAppLogOnError()
+    {
+        if (scenarioContext.ScenarioExecutionStatus == ScenarioExecutionStatus.TestError)
+        {
+            string outputPath = Path.GetFullPath($"app-log-{Guid.NewGuid():N}.txt");
+            appLogContext.SaveToFile(outputPath);
+            outputHelper.AddAttachment(outputPath);
+        }
+    }
+
     [BeforeScenario]
     public void CreateAppHost()
     {
```
