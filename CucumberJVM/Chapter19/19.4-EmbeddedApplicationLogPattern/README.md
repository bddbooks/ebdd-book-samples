# Pattern Differences: 19.4-EmbeddedApplicationLogPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/support/logging/ScenarioContextLogAppender.java](#srctestjavacomwimpappspecssupportloggingscenariocontextlogappenderjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/support/logging/ScenarioContextLogAppender.java

[View file](After/src/test/java/com/wimp/app/specs/support/logging/ScenarioContextLogAppender.java#L16)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/logging/ScenarioContextLogAppender.java#L19-L20)</sub>

```diff
@@ -16,6 +16,8 @@ public class ScenarioContextLogAppender extends AppenderBase<ILoggingEvent> {
 
     private static final DateTimeFormatter ISO_FORMATTER =
         DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").withZone(ZoneId.systemDefault());
+    private static final Level MINIMUM_APP_LOG_LEVEL = Level.INFO;
+    private static final Level MINIMUM_APP_LOG_LEVEL_FOR_SYS = Level.WARN;
 
     private static final ThreadLocal<Scenario> currentScenario = new ThreadLocal<>();
     private static final ThreadLocal<AppLogContext> currentAppLogContext = new ThreadLocal<>();
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/logging/ScenarioContextLogAppender.java#L98-L112)</sub>

```diff
@@ -93,14 +95,21 @@ public class ScenarioContextLogAppender extends AppenderBase<ILoggingEvent> {
 
     private void appendTestLog(ILoggingEvent event, Scenario scenario) {
         boolean isWimpTestLogEvent = event.getLoggerName().startsWith("com.wimp.app.specs");
+        boolean isWimpAppLogEvent = event.getLoggerName().startsWith("com.wimp.app") && !isWimpTestLogEvent;
+        boolean isSystemAppLogEvent = !event.getLoggerName().startsWith("com.wimp");
+        boolean isAppEvent = isWimpAppLogEvent || isSystemAppLogEvent;
 
-        if (!isWimpTestLogEvent) {
+        if ((isWimpAppLogEvent && event.getLevel().toInt() < MINIMUM_APP_LOG_LEVEL.toInt()) ||
+            (isSystemAppLogEvent && event.getLevel().toInt() < MINIMUM_APP_LOG_LEVEL_FOR_SYS.toInt())) {
             return;
         }
 
         String logMessage = event.getFormattedMessage();
-        String logLine = String.format("%s %-5s --- [%s] %s : %s",
-            ISO_FORMATTER.format(event.getInstant()), event.getLevel().toString(), event.getThreadName(), event.getLoggerName(), logMessage);
+        String category = isWimpAppLogEvent ? "WIMP" : isSystemAppLogEvent ? "SYS" : isWimpTestLogEvent ? "TEST" : "???";
+        String indent = isAppEvent ? "  " : "";
+
+        String logLine = String.format("%s %-5s --- [%s] %-4s %s%s : %s",
+            ISO_FORMATTER.format(event.getInstant()), event.getLevel().toString(), event.getThreadName(), category, indent, event.getLoggerName(), logMessage);
 
         scenario.log(logLine);
     }
```
