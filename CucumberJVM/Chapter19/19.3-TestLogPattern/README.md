# Pattern Differences: 19.3-TestLogPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/support/logging/ScenarioContextLogAppender.java](#srctestjavacomwimpappspecssupportloggingscenariocontextlogappenderjava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/Authentication.feature](#srctestresourcescomwimpappspecsauthenticationfeature)

## Detailed Changes

### src/test/java/com/wimp/app/specs/support/logging/ScenarioContextLogAppender.java

[View file](After/src/test/java/com/wimp/app/specs/support/logging/ScenarioContextLogAppender.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/logging/ScenarioContextLogAppender.java#L3-L13)</sub>

```diff
@@ -1,11 +1,16 @@
 package com.wimp.app.specs.support.logging;
 
+import ch.qos.logback.classic.Level;
+import ch.qos.logback.classic.Logger;
 import ch.qos.logback.classic.spi.ILoggingEvent;
 import ch.qos.logback.core.AppenderBase;
 import io.cucumber.java.Scenario;
+import org.jspecify.annotations.NonNull;
+import org.slf4j.LoggerFactory;
 
 import java.time.ZoneId;
 import java.time.format.DateTimeFormatter;
+import java.util.Optional;
 
 public class ScenarioContextLogAppender extends AppenderBase<ILoggingEvent> {
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/logging/ScenarioContextLogAppender.java#L26-L50)</sub>

```diff
@@ -18,9 +23,31 @@ public class ScenarioContextLogAppender extends AppenderBase<ILoggingEvent> {
     public static void onScenarioStart(Scenario scenario, AppLogContext context) {
         currentScenario.set(scenario);
         currentAppLogContext.set(context);
+        // If scenario is tagged with "@log:<level>" tag, we temporarily change
+        // the log level to that.
+        // Note: this solution does not work properly with parallel execution.
+        getTagLogLevel(scenario).ifPresent(ScenarioContextLogAppender::setWimpLogLevel);
+    }
+
+    private static void setWimpLogLevel(Level newLevel) {
+        var logger = (Logger) LoggerFactory.getLogger("com.wimp.app");
+        logger.setLevel(newLevel);
+    }
+
+    private static @NonNull Optional<Level> getTagLogLevel(Scenario scenario) {
+        if (scenario == null) {
+            return Optional.empty();
+        }
+        return scenario.getSourceTagNames().stream()
+            .filter(tagName -> tagName.startsWith("@log:"))
+            .map(tagName -> Level.toLevel(tagName.substring("@log:".length()), null))
+            .findFirst();
     }
 
     public static void onScenarioEnd() {
+        if (getTagLogLevel(getScenario()).isPresent()) {
+            setWimpLogLevel(null); // reset log level back
+        }
         currentScenario.remove();
         currentAppLogContext.remove();
     }
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/logging/ScenarioContextLogAppender.java#L73)</sub>

```diff
@@ -43,6 +70,7 @@ public class ScenarioContextLogAppender extends AppenderBase<ILoggingEvent> {
         // If this thread isn't running a test scenario right now, ignore the log
         if (scenario == null || appLogContext == null) return;
 
+        appendTestLog(event, scenario);
         appendAppLogContext(event, appLogContext);
     }
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/logging/ScenarioContextLogAppender.java#L93-L106)</sub>

```diff
@@ -62,4 +90,18 @@ public class ScenarioContextLogAppender extends AppenderBase<ILoggingEvent> {
         org.slf4j.event.Level slf4jLevel = org.slf4j.event.Level.valueOf(event.getLevel().toString());
         appLogContext.addLogMessage(slf4jLevel, logLine);
     }
+
+    private void appendTestLog(ILoggingEvent event, Scenario scenario) {
+        boolean isWimpTestLogEvent = event.getLoggerName().startsWith("com.wimp.app.specs");
+
+        if (!isWimpTestLogEvent) {
+            return;
+        }
+
+        String logMessage = event.getFormattedMessage();
+        String logLine = String.format("%s %-5s --- [%s] %s : %s",
+            ISO_FORMATTER.format(event.getInstant()), event.getLevel().toString(), event.getThreadName(), event.getLoggerName(), logMessage);
+
+        scenario.log(logLine);
+    }
 }
```

### src/test/resources/com/wimp/app/specs/Authentication.feature

[View file](After/src/test/resources/com/wimp/app/specs/Authentication.feature#L2)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/Authentication.feature#L5)</sub>

```diff
@@ -2,6 +2,7 @@
 
 Rule: Customer needs valid password for login
 
+  @log:debug
   Scenario: A registered customer logs in successfully
     When the customer attempts to log in with valid password
     Then they should be authenticated
```
