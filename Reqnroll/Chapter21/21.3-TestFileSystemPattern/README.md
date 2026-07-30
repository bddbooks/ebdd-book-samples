# Pattern Differences: 21.3-TestFileSystemPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

In order to see the attached application logs in this sample, you need to introduce a bug to the `OrderService.TakeNextOrder()` method (for example change `OrderBy` to `OrderByDescending`).

Uncommenting the `<DefineConstants>` setting in `WIMP.App.csproj` activates this bug without the need to change the `OrderService` class.


## Summary of Changes

- 📝 Modified [WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs](#wimpspecsstepdefinitionsreportingstepdefinitionscs)
- 📝 Modified [WIMP.Specs/Support/Hooks.cs](#wimpspecssupporthookscs)
- ➕ Added [WIMP.Specs/Support/TestFileSystem.cs](#wimpspecssupporttestfilesystemcs)

## Detailed Changes

### WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L16)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L19)</sub>

```diff
@@ -16,7 +16,7 @@ namespace WIMP.Specs.StepDefinitions;
 
 [Binding]
 public class ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver,
-    ILoggerFactory loggerFactory, IFeatureContext featureContext)
+    ILoggerFactory loggerFactory, TestFileSystem testFileSystem)
 {
     private ILogger Logger => loggerFactory.CreateLogger(GetType());
 
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/ReportingStepDefinitions.cs#L28-L29)</sub>

```diff
@@ -25,8 +25,8 @@ public class ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, Repor
     [Given("sales traffic from {string}")]
     public async Task GivenSalesTrafficFrom(string fileName)
     {
-        string csvFilePath = Path.GetFullPath(Path.Combine(
-            featureContext.FeatureInfo.FolderPath, fileName));
+        string csvFilePath = Path.Combine(
+            testFileSystem.FeatureInputFolder, fileName);
         using var reader = new StreamReader(csvFilePath);
         using var csv = new CsvReader(reader, CultureInfo.InvariantCulture);
         Logger.LogInformation("Loading sales records from {CsvFile}", csvFilePath);
```

### WIMP.Specs/Support/Hooks.cs

[View file](After/WIMP.Specs/Support/Hooks.cs#L8)

<sub>[Jump to change](After/WIMP.Specs/Support/Hooks.cs#L11-L12)</sub>

```diff
@@ -8,7 +8,8 @@ namespace WIMP.Specs.Support;
 
 [Binding]
 public class Hooks(AppHostingContext appHostingContext, AppLogContext appLogContext, ReqnrollLoggerProvider reqnrollLoggerProvider,
-    ILoggerFactory loggerFactory, IScenarioContext scenarioContext, IReqnrollOutputHelper outputHelper)
+    ILoggerFactory loggerFactory, IScenarioContext scenarioContext, IReqnrollOutputHelper outputHelper,
+    TestFileSystem testFileSystem)
 {
     private ILogger Logger => loggerFactory.CreateLogger(GetType());
 
```

<sub>[Jump to change](After/WIMP.Specs/Support/Hooks.cs#L21-L22)</sub>

```diff
@@ -17,7 +18,8 @@ public class Hooks(AppHostingContext appHostingContext, AppLogContext appLogCont
     {
         if (scenarioContext.ScenarioExecutionStatus == ScenarioExecutionStatus.TestError)
         {
-            string outputPath = Path.GetFullPath($"app-log-{Guid.NewGuid():N}.txt");
+            string outputPath = Path.Combine(testFileSystem.OutputFolder,
+                testFileSystem.GetScenarioSpecificFileName(".log"));
             appLogContext.SaveToFile(outputPath);
             Logger.LogInformation("Saved app log to {AppLogFile}", outputPath);
             outputHelper.AddAttachment(outputPath);
```

### WIMP.Specs/Support/TestFileSystem.cs

[View file](After/WIMP.Specs/Support/TestFileSystem.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/TestFileSystem.cs#L1-L69)</sub>

```diff
@@ -0,0 +1,69 @@
+using System.Collections;
+using System.Globalization;
+using System.Text;
+
+using Reqnroll;
+
+namespace WIMP.Specs.Support;
+
+public class TestFileSystem(IFeatureContext featureContext, IScenarioContext scenarioContext)
+{
+    public static readonly string TestRunTimestamp =
+        ToPath(DateTime.Now.ToString("s", CultureInfo.InvariantCulture));
+
+    public string OutputFolder =>
+        EnsureFolderExists(Path.Combine(Directory.GetCurrentDirectory(), TestRunTimestamp));
+
+    public string InputFolder => AppContext.BaseDirectory;
+
+    public string FeatureInputFolder =>
+        Path.Combine(InputFolder, featureContext.FeatureInfo.FolderPath);
+
+    public string TempFolder => EnsureFolderExists(
+        Path.Combine(Path.GetTempPath(), "WIMP", TestRunTimestamp));
+
+    public string GetScenarioSpecificFileName(string extension = "")
+    {
+        string featureAsPath = ToPath(featureContext.FeatureInfo.Title);
+        string scenarioAsPath = ToPath(scenarioContext.ScenarioInfo.Title);
+        string baseFileName = $"{featureAsPath}_{scenarioAsPath}";
+        if (scenarioContext.ScenarioInfo.Arguments is { Count: > 0 })
+        {
+            foreach (DictionaryEntry entry in
+                     scenarioContext.ScenarioInfo.Arguments)
+            {
+                baseFileName += $"_{entry.Key}-{entry.Value}";
+            }
+        }
+        return baseFileName + extension;
+    }
+
+    /// <summary>
+    /// Makes string path-compatible, ie removes characters not allowed in path and replaces whitespace with '_'
+    /// </summary>
+    public static string ToPath(string s)
+    {
+        var builder = new StringBuilder(s);
+        foreach (char invalidChar in Path.GetInvalidFileNameChars())
+        {
+            builder.Replace(invalidChar.ToString(), "");
+        }
+        builder.Replace(' ', '_');
+        return builder.ToString();
+    }
+
+    private string EnsureFolderExists(string path)
+    {
+        if (!Directory.Exists(path))
+        {
+            lock (this)
+            {
+                if (!Directory.Exists(path))
+                {
+                    Directory.CreateDirectory(path);
+                }
+            }
+        }
+        return path;
+    }
+}
```
