# Pattern Differences: 18.5-SkippableResetPattern

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

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/DatabaseDriver.java#L3-L14)</sub>

```diff
@@ -1,5 +1,15 @@
 package com.wimp.app.specs.drivers;
 
+import java.util.Collection;
+
 public interface DatabaseDriver {
-    void emptyDatabase();
+    default void emptyDatabase() {
+        emptyDatabase(null);
+    }
+
+    void emptyDatabase(Collection<String> exceptTables);
+
+    boolean wasTableModified(String tableName);
+
+    void resetTableModificationTracking();
 }
```

### src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java#L9)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java#L12-L17)</sub>

```diff
@@ -9,9 +9,12 @@ import org.springframework.stereotype.Component;
 
 import javax.sql.DataSource;
 import java.sql.Connection;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
+import java.util.Collection;
 import java.util.List;
 
 @Component
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java#L39-L49)</sub>

```diff
@@ -33,12 +36,17 @@ public class RealDatabaseDriver implements DatabaseDriver {
         // point: DatabaseConfiguration configures hibernate.hbm2ddl.auto=update
         // on the EntityManagerFactory, which creates/updates tables and columns
         // as needed whenever it is initialized.
+
+        // Setting up our own modification-tracking infrastructure.
+        ensureModificationTrackingInfrastructure();
     }
 
     @Override
-    public void emptyDatabase() {
-        log.info("Emptying database");
-        List<String> tablesToEmpty = getAllTables();
+    public void emptyDatabase(Collection<String> exceptTables) {
+        log.info("Emptying database, except {}", exceptTables);
+        List<String> tablesToEmpty = getAllTables().stream()
+            .filter(table -> exceptTables == null || !exceptTables.contains(table))
+            .toList();
         emptyTables(tablesToEmpty);
     }
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java#L81-L146)</sub>

```diff
@@ -70,4 +78,70 @@ public class RealDatabaseDriver implements DatabaseDriver {
 
         return tableNames;
     }
+
+    private void ensureModificationTrackingInfrastructure() {
+        try (Connection connection = dataSource.getConnection();
+             Statement statement = connection.createStatement()) {
+            statement.execute("""
+                CREATE TABLE IF NOT EXISTS MOD_TRACKING
+                (
+                    TABLE_NAME VARCHAR(64) NOT NULL,
+                    IS_MODIFIED TINYINT(1) NOT NULL DEFAULT 0,
+                    PRIMARY KEY (TABLE_NAME)
+                );
+                """);
+            statement.execute("""
+                INSERT INTO MOD_TRACKING (TABLE_NAME, IS_MODIFIED)
+                VALUES ('ANY', 1)
+                ON DUPLICATE KEY UPDATE IS_MODIFIED = 1;
+                """);
+
+            for (String tableName : getAllTables()) {
+                for (String eventName : new String[] {"INSERT", "UPDATE", "DELETE"}) {
+                    String triggerName = "TRG_" + tableName + "_TRACK_" + eventName;
+                    statement.execute("DROP TRIGGER IF EXISTS " + triggerName + ";");
+                    statement.execute("""
+                        CREATE TRIGGER %s
+                        AFTER %s ON %s
+                        FOR EACH ROW
+                        INSERT INTO MOD_TRACKING (TABLE_NAME, IS_MODIFIED)
+                        VALUES ('%s', 1)
+                        ON DUPLICATE KEY UPDATE IS_MODIFIED = 1;
+                        """.formatted(triggerName, eventName, tableName, tableName));
+                }
+            }
+        } catch (SQLException ex) {
+            throw new IllegalStateException("Failed to set up modification tracking infrastructure", ex);
+        }
+    }
+
+    @Override
+    public boolean wasTableModified(String tableName) {
+        try (Connection connection = dataSource.getConnection();
+             PreparedStatement statement = connection.prepareStatement("""
+                 SELECT COALESCE((
+                     SELECT IS_MODIFIED
+                     FROM MOD_TRACKING
+                     WHERE TABLE_NAME = ? OR TABLE_NAME = 'ANY'
+                     LIMIT 1
+                 ), 0);
+                 """)) {
+            statement.setString(1, tableName);
+            try (ResultSet resultSet = statement.executeQuery()) {
+                return resultSet.next() && resultSet.getBoolean(1);
+            }
+        } catch (SQLException ex) {
+            throw new IllegalStateException("Failed to read table modification tracking for " + tableName, ex);
+        }
+    }
+
+    @Override
+    public void resetTableModificationTracking() {
+        try (Connection connection = dataSource.getConnection();
+             Statement statement = connection.createStatement()) {
+            statement.execute("TRUNCATE TABLE MOD_TRACKING;");
+        } catch (SQLException ex) {
+            throw new IllegalStateException("Failed to reset table modification tracking", ex);
+        }
+    }
 }
```

### src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java#L3)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java#L6-L23)</sub>

```diff
@@ -3,12 +3,24 @@ package com.wimp.app.specs.drivers;
 import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
 import org.springframework.stereotype.Component;
 
+import java.util.Collection;
+
 @Component
 @ConditionalOnProperty(name = "test.database.use-stub", havingValue = "true")
 public class StubDatabaseDriver implements DatabaseDriver {
 
     @Override
-    public void emptyDatabase() {
+    public void emptyDatabase(Collection<String> exceptTables) {
+        // nop
+    }
+
+    @Override
+    public boolean wasTableModified(String tableName) {
+        return true; // treat it modified, seeding is anyway "free"
+    }
+
+    @Override
+    public void resetTableModificationTracking() {
         // nop
     }
 }
```

### src/test/java/com/wimp/app/specs/support/Hooks.java

[View file](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L11)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L14)</sub>

```diff
@@ -11,6 +11,7 @@ import java.util.List;
 
 public class Hooks {
     private static final Logger log = LoggerFactory.getLogger(Hooks.class);
+    private static final String MENU_TABLE_NAME = "menu_items";
 
     private final DatabaseDriver databaseDriver;
     private final MenuBackdoorDriver menuBackdoorDriver;
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L26-L31)</sub>

```diff
@@ -22,8 +23,12 @@ public class Hooks {
 
     @Before(order = 0)
     public void resetDatabase() {
-        databaseDriver.emptyDatabase();
-        seedMenuData();
+        boolean wasMenuModified = databaseDriver.wasTableModified(MENU_TABLE_NAME);
+        databaseDriver.emptyDatabase(!wasMenuModified ? List.of(MENU_TABLE_NAME) : List.of());
+        if (wasMenuModified) {
+            seedMenuData();
+        }
+        databaseDriver.resetTableModificationTracking();
     }
 
     private void seedMenuData() {
```
