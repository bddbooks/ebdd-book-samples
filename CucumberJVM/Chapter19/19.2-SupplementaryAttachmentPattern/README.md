# Pattern Differences: 19.2-SupplementaryAttachmentPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

In order to see the attached application logs in this sample, you need to introduce a bug to the `OrderService.TakeNextOrder()` method (for example change `OrderBy` to `OrderByDescending`).

Setting `app.simulation.bug` to `true` in `src/main/resources/application.properties` activates this bug without the need to change the `OrderService` class.


## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/support/Hooks.java](#srctestjavacomwimpappspecssupporthooksjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/support/Hooks.java

[View file](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L3-L32)</sub>

```diff
@@ -1,14 +1,34 @@
 package com.wimp.app.specs.support;
 
+import com.wimp.app.specs.support.logging.AppLogContext;
+import io.cucumber.java.*;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
+import java.nio.file.Paths;
+
 public class Hooks {
     private static final Logger log = LoggerFactory.getLogger(Hooks.class);
 
     private final TestFileSystem testFileSystem;
+    private final AppLogContext appLogContext;
 
-    public Hooks(TestFileSystem testFileSystem) {
+    public Hooks(TestFileSystem testFileSystem, AppLogContext appLogContext) {
         this.testFileSystem = testFileSystem;
+        this.appLogContext = appLogContext;
+    }
+
+    // The after-scenario hook with a high order number ensures that it captures
+    // the log before any other resources are disposed of.
+    @After(order = 100)
+    public void saveAppLogOnError(Scenario scenario) {
+        if (scenario.getStatus() == Status.FAILED) {
+            // A detailed discussion of the TestFileSystem class can be found in chapter 21: Test File System pattern
+            var logFileName = testFileSystem.getScenarioSpecificFileName(".log");
+            var outputPath = Paths.get(testFileSystem.getOutputFolder(), logFileName).toString();
+            var logContent = appLogContext.saveToFile(outputPath);
+            log.info("Saved app log to {}", outputPath);
+            scenario.attach(logContent, "text/plain", logFileName);
+        }
     }
 }
```
