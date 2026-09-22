# Pattern Differences: 18.3-CleanStartPattern

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
@@ -1,5 +1,5 @@
 package com.wimp.app.specs.drivers;
 
 public interface DatabaseDriver {
-    void recreateDatabase();
+    void emptyDatabase();
 }
```

### src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java#L16)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java#L19-L33)</sub>

```diff
@@ -16,13 +16,20 @@ public class RealDatabaseDriver implements DatabaseDriver {
 
     public RealDatabaseDriver(EntityManagerFactory entityManagerFactory) {
         this.entityManagerFactory = entityManagerFactory;
+        upgradeSchemaIfNeeded();
+    }
+
+    private void upgradeSchemaIfNeeded() {
+        // Hibernate already ensures the database schema is up-to-date at this
+        // point: DatabaseConfiguration configures hibernate.hbm2ddl.auto=update
+        // on the EntityManagerFactory, which creates/updates tables and columns
+        // as needed whenever it is initialized.
     }
 
     @Override
-    public void recreateDatabase() {
-        log.info("Re-creating database");
+    public void emptyDatabase() {
+        log.info("Emptying database");
         var sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
-        sessionFactory.getSchemaManager().drop(true);
-        sessionFactory.getSchemaManager().create(true);
+        sessionFactory.getSchemaManager().truncate();
     }
 }
```

### src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java#L8)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java#L11)</sub>

```diff
@@ -8,7 +8,7 @@ import org.springframework.stereotype.Component;
 public class StubDatabaseDriver implements DatabaseDriver {
 
     @Override
-    public void recreateDatabase() {
+    public void emptyDatabase() {
         // nop
     }
 }
```

### src/test/java/com/wimp/app/specs/support/Hooks.java

[View file](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L12)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L15)</sub>

```diff
@@ -12,6 +12,6 @@ public class Hooks {
 
     @Before(order = 0)
     public void resetDatabase() {
-        databaseDriver.recreateDatabase();
+        databaseDriver.emptyDatabase();
     }
 }
```
