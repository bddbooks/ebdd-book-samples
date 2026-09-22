# Pattern Differences: 19.6-FailFastCheckPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

In order to trigger a fail-fast check errors do one of the following steps:

* Change the scenario to load CSV data from `realistic-traffic-empty.csv` instead of `realistic-traffic.csv`.
* Return a failed `ServiceResult` from `TestDataService.PrepareSalesTraffic()` method. 
  * Uncommenting the `<DefineConstants>` setting in `WIMP.App.csproj` activates this bug without the need to change the `TestDataService` class.


## Summary of Changes

- 📝 Modified [WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs](#wimpspecsstepdefinitionsreportingstepdefinitionscs)
- 📝 Modified [WIMP.Specs/Support/RestApiContext.cs](#wimpspecssupportrestapicontextcs)

## Detailed Changes

### WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L28)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L31-L37)</sub>

```diff
@@ -28,6 +28,13 @@ public class ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, Repor
         var salesTraffic =
             csv.GetRecords<DailyPizzaSales>().ToArray();
 
+        // Fail-fast check: If the CSV file is empty, we can fail because that
+        // might be a sign of some environmental error.
+        if (salesTraffic.Length == 0)
+        {
+            throw new InvalidOperationException($"The CSV file '{csvFilePath}' was empty!");
+        }
+
         await backdoorApiDriver
             .PrepareSalesTraffic(salesTraffic)
             .Execute();
```

### WIMP.Specs/Support/RestApiContext.cs

[View file](After/WIMP.Specs/Support/RestApiContext.cs#L36)

<sub>[Jump to change](After/WIMP.Specs/Support/RestApiContext.cs#L39-L43)</sub>

```diff
@@ -36,9 +36,11 @@ public class RestApiContext(AppHostingContext appHostingContext, ILoggerFactory
         if (response.StatusCode != successStatusCode)
         {
             string errorMessage = await ReadErrorMessage(response);
-            Logger.LogWarning(
-                "{ActionName} failed with status code {StatusCode}. " +
-                "Error message: '{ErrorMessage}'", actionName, response.StatusCode, errorMessage);
+            // Fail-fast check: if the response status code was not the expected,
+            // we fail immediately
+            throw new TestActionFailedException(
+                $"{actionName} failed with status code {response.StatusCode}. " +
+                $"Error message: '{errorMessage}'");
         }
 
         return typeof(TResult) == typeof(VoidReturn)
```
