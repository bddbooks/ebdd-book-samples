# Pattern Differences: 21.1-TestEnvironmentInitializationPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

Note: Using real database for testing is enabled by default for this sample. You can disable it by setting `test.database.use-stub` to `true` from command line or pom.xml.

This sample requires a MySQL database to be running using Docker, therefore you need to have Docker installed and running on your machine.


## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/support/Hooks.java](#srctestjavacomwimpappspecssupporthooksjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/support/Hooks.java

[View file](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L15)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L18-L25)</sub>

```diff
@@ -15,6 +15,14 @@ public class Hooks {
         this.databaseDriver = databaseDriver;
     }
 
+    @BeforeAll
+    public static void ensureDockerInstalled() {
+        // One-time initialization can be performed here.
+        // for example here you can ensure that Docker (that is used for hosting
+        // the database) is installed.
+        log.info("Simulating installation & configuration of Docker...");
+    }
+
     @Before(order = 0)
     public void resetDatabase() {
         databaseDriver.emptyDatabase();
```
