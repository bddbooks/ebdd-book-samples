# Pattern Differences: 19.5-CorroborationCheckPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

In order to trigger order rejections that do not modify the test result, but represent an issue that should be alerted for, you need to introduce a warning log to `OrderService.PlaceOrder()` method.

Setting `app.simulation.bug` to `true` in `src/main/resources/application.properties` activates such an issue without the need to change the `OrderService` class.


## Summary of Changes

- ➕ Added [src/test/java/com/wimp/app/specs/support/CorroborationCheckException.java](#srctestjavacomwimpappspecssupportcorroborationcheckexceptionjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/Hooks.java](#srctestjavacomwimpappspecssupporthooksjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/logging/AppLogContext.java](#srctestjavacomwimpappspecssupportloggingapplogcontextjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/support/CorroborationCheckException.java

[View file](After/src/test/java/com/wimp/app/specs/support/CorroborationCheckException.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/CorroborationCheckException.java#L1-L7)</sub>

```diff
@@ -0,0 +1,7 @@
+package com.wimp.app.specs.support;
+
+public class CorroborationCheckException extends RuntimeException {
+    public CorroborationCheckException(String message) {
+        super(message);
+    }
+}
```

### src/test/java/com/wimp/app/specs/support/Hooks.java

[View file](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L18)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L21-L31)</sub>

```diff
@@ -18,6 +18,17 @@ public class Hooks {
         this.appLogContext = appLogContext;
     }
 
+    // The after-scenario hook with a high order number ensures that it captures
+    // the log before any other resources are disposed of.
+    // It runs before the 'saveAppLogOnError' hook so that the log should be
+    // saved even in a case of a corroboration check failure.
+    @After(order = 110)
+    public void checkAppHealth(Scenario scenario) {
+        if (scenario.getStatus() == Status.PASSED) {
+            appLogContext.checkAppHealth();
+        }
+    }
+
     // The after-scenario hook with a high order number ensures that it captures
     // the log before any other resources are disposed of.
     @After(order = 100)
```

### src/test/java/com/wimp/app/specs/support/logging/AppLogContext.java

[View file](After/src/test/java/com/wimp/app/specs/support/logging/AppLogContext.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/logging/AppLogContext.java#L3)</sub>

```diff
@@ -1,5 +1,6 @@
 package com.wimp.app.specs.support.logging;
 
+import com.wimp.app.specs.support.CorroborationCheckException;
 import io.cucumber.spring.ScenarioScope;
 import org.slf4j.event.Level;
 import org.springframework.stereotype.Component;
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/logging/AppLogContext.java#L24)</sub>

```diff
@@ -20,6 +21,7 @@ import java.util.concurrent.ConcurrentLinkedQueue;
 public class AppLogContext {
 
     private final ConcurrentLinkedQueue<String> logMessages = new ConcurrentLinkedQueue<>();
+    private final ConcurrentLinkedQueue<String> healthIssues = new ConcurrentLinkedQueue<>();
 
     /**
      * Returns an unmodifiable view of the captured log messages.
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/logging/AppLogContext.java#L35-L58)</sub>

```diff
@@ -30,6 +32,30 @@ public class AppLogContext {
 
     public void addLogMessage(Level logLevel, String logMessage) {
         logMessages.add(logMessage);
+        if (logLevel.toInt() >= Level.WARN.toInt()) {
+            healthIssues.add(logMessage);
+        }
+    }
+
+    public void checkAppHealth() {
+        if (!healthIssues.isEmpty()) {
+            String issueText = String.join(System.lineSeparator(), healthIssues);
+            throw new CorroborationCheckException(
+                "The application log contains warnings or errors:" + System.lineSeparator() + issueText
+            );
+        }
+    }
+
+    /**
+     * Suppresses the health issues that have been collected so far. This can be
+     * used for special tests where application warnings or errors are
+     * expected.
+     * <p>
+     * It can be invoked via
+     * ScenarioContextLogAppender.getAppLogContext().suppressAppHealthIssues().
+     */
+    public void suppressAppHealthIssues() {
+        healthIssues.clear();
     }
 
     public String saveToFile(String outputPath) {
```
