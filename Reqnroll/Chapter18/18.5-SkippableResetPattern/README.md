# Pattern Differences: 18.5-SkippableResetPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

This sample requires a MySQL database to be running. The following instructions are for setting up a MySQL database in a Docker container.

* Create and start container (first time):  
  `docker run --name wimp-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=wimp_db -p 3306:3306 -d mysql:8.4`

* Stop container:  
  `docker stop wimp-mysql`

* Start existing container again:  
  `docker start wimp-mysql`

* Restart container:  
  `docker restart wimp-mysql`

* Start from scratch (delete container + all DB data in it):
  ```
  docker stop wimp-mysql
  docker rm wimp-mysql
  docker run --name wimp-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=wimp_db -p 3306:3306 -d mysql:8.4
  ```


## Summary of Changes

- 📝 Modified [WIMP.Specs/Drivers/DatabaseDriver.cs](#wimpspecsdriversdatabasedrivercs)
- 📝 Modified [WIMP.Specs/Drivers/IDatabaseDriver.cs](#wimpspecsdriversidatabasedrivercs)
- 📝 Modified [WIMP.Specs/Drivers/StubDatabaseDriver.cs](#wimpspecsdriversstubdatabasedrivercs)
- 📝 Modified [WIMP.Specs/Support/Hooks.cs](#wimpspecssupporthookscs)

## Detailed Changes

### WIMP.Specs/Drivers/DatabaseDriver.cs

[View file](After/WIMP.Specs/Drivers/DatabaseDriver.cs#L8)

<sub>[Jump to change](After/WIMP.Specs/Drivers/DatabaseDriver.cs#L11-L17)</sub>

```diff
@@ -8,9 +8,13 @@ namespace WIMP.Specs.Drivers;
 
 public class DatabaseDriver(IDbContextFactory<WimpDbContext> dbContextFactory) : IDatabaseDriver
 {
-    public async Task EmptyDatabase()
+    public async Task EmptyDatabase(IReadOnlyCollection<string>? exceptTables = null)
     {
         var tablesToEmpty = GetAllTables();
+        if (exceptTables != null)
+        {
+            tablesToEmpty = tablesToEmpty.Where(t => !exceptTables.Contains(t));
+        }
         await EmptyTables(tablesToEmpty);
     }
 
```

<sub>[Jump to change](After/WIMP.Specs/Drivers/DatabaseDriver.cs#L63-L135)</sub>

```diff
@@ -56,7 +60,77 @@ public class DatabaseDriver(IDbContextFactory<WimpDbContext> dbContextFactory) :
         {
             await using var db = await dbContextFactory.CreateDbContextAsync();
             await db.Database.MigrateAsync();
+            await EnsureModificationTrackingInfrastructure(db);
             databaseInitialized = true;
         }
     }
+
+    private async Task EnsureModificationTrackingInfrastructure(WimpDbContext db)
+    {
+        await using var connection = (MySqlConnection)db.Database.GetDbConnection();
+        await connection.OpenAsync();
+
+        await using (var createTable = new MySqlCommand("""
+            CREATE TABLE IF NOT EXISTS MOD_TRACKING
+            (
+                TABLE_NAME VARCHAR(64) NOT NULL,
+                IS_MODIFIED TINYINT(1) NOT NULL DEFAULT 0,
+                PRIMARY KEY (TABLE_NAME)
+            );
+            INSERT INTO MOD_TRACKING (TABLE_NAME, IS_MODIFIED)
+            VALUES ('ANY', 1)
+            ON DUPLICATE KEY UPDATE IS_MODIFIED = 1;
+            """, connection))
+        {
+            await createTable.ExecuteNonQueryAsync();
+        }
+
+        foreach (string tableName in GetAllTables())
+        {
+            foreach (string eventName in new[] { "INSERT", "UPDATE", "DELETE" })
+            {
+                string triggerName = $"TRG_{tableName}_TRACK_{eventName}";
+                await using var createTrigger = new MySqlCommand($"""
+                    DROP TRIGGER IF EXISTS {triggerName};
+                    CREATE TRIGGER {triggerName}
+                    AFTER {eventName} ON {tableName}
+                    FOR EACH ROW
+                    INSERT INTO MOD_TRACKING (TABLE_NAME, IS_MODIFIED)
+                    VALUES ('{tableName}', 1)
+                    ON DUPLICATE KEY UPDATE IS_MODIFIED = 1;
+                    """, connection);
+                await createTrigger.ExecuteNonQueryAsync();
+            }
+        }
+    }
+
+    public async Task<bool> WasTableModified(string tableName)
+    {
+        await using var db = await dbContextFactory.CreateDbContextAsync();
+        await using var connection = (MySqlConnection)db.Database.GetDbConnection();
+        await connection.OpenAsync();
+
+        await using var cmd = new MySqlCommand("""
+            SELECT COALESCE((
+                SELECT IS_MODIFIED
+                FROM MOD_TRACKING
+                WHERE TABLE_NAME = @tableName OR TABLE_NAME = 'ANY'
+                LIMIT 1
+            ), 0);
+            """, connection);
+        cmd.Parameters.AddWithValue("@tableName", tableName);
+
+        object? result = await cmd.ExecuteScalarAsync();
+        return result is not null && Convert.ToBoolean(result);
+    }
+
+    public async Task ResetTableModificationTracking()
+    {
+        await using var db = await dbContextFactory.CreateDbContextAsync();
+        await using var connection = (MySqlConnection)db.Database.GetDbConnection();
+        await connection.OpenAsync();
+
+        await using var cmd = new MySqlCommand("TRUNCATE TABLE MOD_TRACKING;", connection);
+        await cmd.ExecuteNonQueryAsync();
+    }
 }
```

### WIMP.Specs/Drivers/IDatabaseDriver.cs

[View file](After/WIMP.Specs/Drivers/IDatabaseDriver.cs#L2)

<sub>[Jump to change](After/WIMP.Specs/Drivers/IDatabaseDriver.cs#L5-L8)</sub>

```diff
@@ -2,6 +2,8 @@ namespace WIMP.Specs.Drivers;
 
 public interface IDatabaseDriver
 {
-    public Task EmptyDatabase();
+    public Task EmptyDatabase(IReadOnlyCollection<string>? exceptTables = null);
     public Task UpgradeSchemaIfNeeded();
+    public Task<bool> WasTableModified(string tableName);
+    public Task ResetTableModificationTracking();
 }
```

### WIMP.Specs/Drivers/StubDatabaseDriver.cs

[View file](After/WIMP.Specs/Drivers/StubDatabaseDriver.cs#L2)

<sub>[Jump to change](After/WIMP.Specs/Drivers/StubDatabaseDriver.cs#L5)</sub>

```diff
@@ -2,7 +2,7 @@ namespace WIMP.Specs.Drivers;
 
 public class StubDatabaseDriver : IDatabaseDriver
 {
-    public Task EmptyDatabase()
+    public Task EmptyDatabase(IReadOnlyCollection<string>? exceptTables = null)
     {
         //nop
         return Task.CompletedTask;
```

<sub>[Jump to change](After/WIMP.Specs/Drivers/StubDatabaseDriver.cs#L16-L26)</sub>

```diff
@@ -13,4 +13,15 @@ public class StubDatabaseDriver : IDatabaseDriver
         //nop
         return Task.CompletedTask;
     }
+
+    public Task<bool> WasTableModified(string tableName)
+    {
+        return Task.FromResult(true); // treat it modified, seeding is anyway "free"
+    }
+
+    public Task ResetTableModificationTracking()
+    {
+        //nop
+        return Task.CompletedTask;
+    }
 }
```

### WIMP.Specs/Support/Hooks.cs

[View file](After/WIMP.Specs/Support/Hooks.cs#L12)

<sub>[Jump to change](After/WIMP.Specs/Support/Hooks.cs#L15-L21)</sub>

```diff
@@ -12,8 +12,13 @@ public class Hooks(AppHostingContext appHostingContext, IDatabaseDriver database
     public async Task ResetDatabase()
     {
         await databaseDriver.UpgradeSchemaIfNeeded();
-        await databaseDriver.EmptyDatabase();
-        SeedMenuData();
+        bool wasMenuModified = await databaseDriver.WasTableModified("MENU");
+        await databaseDriver.EmptyDatabase(exceptTables: !wasMenuModified ? ["MENU"] : []);
+        if (wasMenuModified)
+        {
+            SeedMenuData();
+        }
+        await databaseDriver.ResetTableModificationTracking();
     }
 
     private void SeedMenuData()
```
