# Pattern Differences: 18.2-ResetBeforePattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

Note: Using real database for testing is enabled by default for this sample. You can disable it by setting `test.database.use-stub` to `true` from command line or pom.xml.

This sample requires a MySQL database to be running using Docker, therefore you need to have Docker installed and running on your machine.


## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/drivers/DatabaseDriver.java](#srctestjavacomwimpappspecsdriversdatabasedriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java](#srctestjavacomwimpappspecsdriversrealdatabasedriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java](#srctestjavacomwimpappspecsdriversstubdatabasedriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/Hooks.java](#srctestjavacomwimpappspecssupporthooksjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/drivers/DatabaseDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/DatabaseDriver.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/DatabaseDriver.java#L4)</sub>

```diff
@@ -1,6 +1,5 @@
 package com.wimp.app.specs.drivers;
 
 public interface DatabaseDriver {
-    void createDatabase();
-    void dropDatabase();
+    void recreateDatabase();
 }
```

### src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java#L19)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java#L22-L26)</sub>

```diff
@@ -19,16 +19,10 @@ public class RealDatabaseDriver implements DatabaseDriver {
     }
 
     @Override
-    public void createDatabase() {
-        log.info("Creating database");
-        var sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
-        sessionFactory.getSchemaManager().create(true);
-    }
-
-    @Override
-    public void dropDatabase() {
-        log.info("dropping database");
+    public void recreateDatabase() {
+        log.info("Re-creating database");
         var sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
         sessionFactory.getSchemaManager().drop(true);
+        sessionFactory.getSchemaManager().create(true);
     }
 }
```

### src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java#L8)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java#L11)</sub>

```diff
@@ -8,12 +8,7 @@ import org.springframework.stereotype.Component;
 public class StubDatabaseDriver implements DatabaseDriver {
 
     @Override
-    public void createDatabase() {
-        // nop
-    }
-
-    @Override
-    public void dropDatabase() {
+    public void recreateDatabase() {
         // nop
     }
 }
```

### src/test/java/com/wimp/app/specs/support/Hooks.java

[View file](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L11)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L14-L15)</sub>

```diff
@@ -11,12 +11,7 @@ public class Hooks {
     }
 
     @Before(order = 0)
-    public void createDatabase() {
-        databaseDriver.createDatabase();
-    }
-
-    @After
-    public void dropDatabase() {
-        databaseDriver.dropDatabase();
+    public void resetDatabase() {
+        databaseDriver.recreateDatabase();
     }
 }
```
