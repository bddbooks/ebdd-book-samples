# Pattern Differences: 21.3-TestFileSystemPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

In order to see the attached application logs in this sample, you need to introduce a bug to the `OrderService.TakeNextOrder()` method (for example change `OrderBy` to `OrderByDescending`).

Setting `app.simulation.bug` to `true` in `src/main/resources/application.properties` activates this bug without the need to change the `OrderService` class.


## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsreportingstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/Hooks.java](#srctestjavacomwimpappspecssupporthooksjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/TestFileSystem.java](#srctestjavacomwimpappspecssupporttestfilesystemjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L5)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L8)</sub>

```diff
@@ -5,13 +5,13 @@ import com.wimp.app.models.SalesReport;
 import com.wimp.app.specs.drivers.BackdoorApiDriver;
 import com.wimp.app.specs.drivers.ReportingApiDriver;
 import com.wimp.app.specs.support.ReportPrinter;
+import com.wimp.app.specs.support.TestFileSystem;
 import io.cucumber.datatable.DataTable;
 import io.cucumber.java.en.*;
 
 import java.math.BigDecimal;
 import java.nio.file.Files;
 import java.nio.file.Path;
-import java.nio.file.Paths;
 import java.time.LocalDate;
 import java.util.*;
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L23-L35)</sub>

```diff
@@ -20,18 +20,19 @@ import static org.assertj.core.api.Assertions.assertThat;
 public class ReportingStepDefinitions {
     private final BackdoorApiDriver backdoorApiDriver;
     private final ReportingApiDriver reportingApiDriver;
+    private final TestFileSystem testFileSystem;
     private SalesReport generatedReport;
 
-    public ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver) {
+    public ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver, TestFileSystem testFileSystem) {
         this.backdoorApiDriver = backdoorApiDriver;
         this.reportingApiDriver = reportingApiDriver;
+        this.testFileSystem = testFileSystem;
     }
 
     @Given("sales traffic from {string}")
     public void salesTrafficFrom(String fileName) throws Exception {
-        // Temporary solution: hard-coded feature file folder.
-        var featureFileFolder = Paths.get(System.getProperty("user.dir"), "src/test/resources/com/wimp/app/specs").toString();
-        Path path = Path.of(featureFileFolder, fileName);
+        // Retrieving the folder of the current feature file
+        Path path = Path.of(testFileSystem.getFeatureInputFolder(), fileName);
         List<String> lines = Files.readAllLines(path);
         if (lines.size() < 2) throw new IllegalArgumentException("The CSV file '" + path + "' was empty!");
         var headers = Arrays.asList(lines.getFirst().split(","));
```

### src/test/java/com/wimp/app/specs/support/Hooks.java

[View file](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L5)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L13-L28)</sub>

```diff
@@ -5,33 +5,27 @@ import io.cucumber.java.*;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
-import java.io.IOException;
-import java.nio.file.Files;
 import java.nio.file.Paths;
-import java.util.UUID;
 
 public class Hooks {
     private static final Logger log = LoggerFactory.getLogger(Hooks.class);
 
+    private final TestFileSystem testFileSystem;
     private final AppLogContext appLogContext;
 
-    public Hooks(AppLogContext appLogContext) {
+    public Hooks(TestFileSystem testFileSystem, AppLogContext appLogContext) {
+        this.testFileSystem = testFileSystem;
         this.appLogContext = appLogContext;
     }
 
     // The after-scenario hook with a high order number ensures that it captures
     // the log before any other resources are disposed of.
     @After(order = 100)
-    public void saveAppLogOnError(Scenario scenario) throws IOException {
+    public void saveAppLogOnError(Scenario scenario) {
         if (scenario.getStatus() == Status.FAILED) {
-            // Calculating a unique log file name
-            var logFileName = "app-log-%s.log".formatted(UUID.randomUUID().toString());
-            // Calculating the expected output folder.
-            var outputFolder = Paths.get(System.getProperty("user.dir"), "target", "wimp-output");
-            if (!Files.exists(outputFolder)) {
-                Files.createDirectories(outputFolder);
-            }
-            var outputPath = Paths.get(outputFolder.toString(), logFileName).toString();
+            // Retrieving a log file name that is named as the scenario and located in the expected output folder.
+            var logFileName = testFileSystem.getScenarioSpecificFileName(".log");
+            var outputPath = Paths.get(testFileSystem.getOutputFolder(), logFileName).toString();
             var logContent = appLogContext.saveToFile(outputPath);
             log.info("Saved app log to {}", outputPath);
             scenario.attach(logContent, "text/plain", logFileName);
```

### src/test/java/com/wimp/app/specs/support/TestFileSystem.java

[View file](After/src/test/java/com/wimp/app/specs/support/TestFileSystem.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/TestFileSystem.java#L1-L113)</sub>

```diff
@@ -0,0 +1,113 @@
+package com.wimp.app.specs.support;
+
+import io.cucumber.java.Before;
+import io.cucumber.java.Scenario;
+
+import java.io.IOException;
+import java.nio.file.Files;
+import java.nio.file.Path;
+import java.nio.file.Paths;
+import java.time.LocalDateTime;
+import java.time.format.DateTimeFormatter;
+
+/**
+ * Helper class to make accessing the file system for testing easier. See Test
+ * File System pattern for details.
+ * <p>
+ * This class is a Cucumber glue class (because it uses a hook to capture the
+ * current Scenario object), so Cucumber manages the lifetime of it. Because of
+ * this, it cannot be registered as a normal Spring Boot component.
+ */
+public class TestFileSystem {
+    /**
+     * This setting needs to be adjusted according to the project structure.
+     */
+    public static final String TEST_RESOURCES_ROOT = "src/test/resources";
+
+    public static final String TEST_RUN_TIMESTAMP =
+        toPath(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH_mm_ss")));
+
+    private Scenario scenario;
+
+    @Before
+    public void captureCurrentScenario(Scenario scenario) {
+        this.scenario = scenario;
+    }
+
+    public String getOutputFolder() {
+        return ensureFolderExists(Paths.get(System.getProperty("user.dir"), "target", "wimp-output", TEST_RUN_TIMESTAMP).toString());
+    }
+
+    /**
+     * Uses the resources folder as the default input folder.
+     */
+    public String getInputFolder() {
+        return Paths.get(System.getProperty("user.dir"), TEST_RESOURCES_ROOT).toAbsolutePath().toString();
+    }
+
+    public String getFeatureInputFolder() {
+        // Cucumber URI format looks like: "classpath:features/MyFeature.feature" or "file:src/test/resources/..." or "file:///C:/project/src/test/resources/..."
+        var scenarioUri = scenario.getUri();
+        String fullFeatureFilePath;
+        if (scenarioUri.getScheme().equals("classpath")) {
+            fullFeatureFilePath = Paths.get(getInputFolder(), scenarioUri.getSchemeSpecificPart()).toString();
+        } else if (scenarioUri.getScheme().equals("file")) {
+            if (scenarioUri.isAbsolute() && scenarioUri.getSchemeSpecificPart().startsWith("/")) {
+                fullFeatureFilePath = Paths.get(scenarioUri).toAbsolutePath().toString();
+            } else {
+                String schemeSpecificPart = scenarioUri.getSchemeSpecificPart(); // e.g., "src/test/resources/"
+                fullFeatureFilePath = Paths.get(System.getProperty("user.dir")).resolve(schemeSpecificPart).toAbsolutePath().toString();
+            }
+        } else {
+            throw new IllegalStateException("Unable to get feature file path from scenario URI: " + scenarioUri);
+        }
+        return fullFeatureFilePath.replaceFirst("[/\\\\]?[^/\\\\]*\\.feature$", "");
+    }
+
+    public String getTempFolder() {
+        return ensureFolderExists(
+            Paths.get(System.getProperty("java.io.tmpdir"), "WIMP", TEST_RUN_TIMESTAMP).toString()
+        );
+    }
+
+    public String getScenarioSpecificFileName() {
+        return getScenarioSpecificFileName("");
+    }
+
+    public String getScenarioSpecificFileName(String extension) {
+        String scenarioNameAsPath = toPath(scenario.getName());
+
+        // Extract Cucumber's unique ID for the current row (e.g., "classpath:features/test.feature:12")
+        String uniqueRowId = toPath(scenario.getId());
+
+        return scenarioNameAsPath + "_" + uniqueRowId + extension;
+    }
+
+    /**
+     * Makes a string path-compatible by removing invalid symbols and replacing
+     * whitespaces with underscores.
+     */
+    public static String toPath(String s) {
+        if (s == null) {
+            return "";
+        }
+        String clean = s.replaceAll("[\\\\/:*?\"<>|\\x00-\\x1F]", "");
+        return clean.replace(' ', '_');
+    }
+
+    private String ensureFolderExists(String pathStr) {
+        Path path = Paths.get(pathStr);
+        if (!Files.exists(path)) {
+            synchronized (this) {
+                if (!Files.exists(path)) {
+                    try {
+                        Files.createDirectories(path);
+                    } catch (IOException e) {
+                        throw new RuntimeException("Failed to generate test directory: " + pathStr, e);
+                    }
+                }
+            }
+        }
+        return pathStr;
+    }
+}
```
