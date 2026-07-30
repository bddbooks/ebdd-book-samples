# Pattern Differences: 18.3-CleanStartPattern

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

[View file](After/WIMP.Specs/Drivers/DatabaseDriver.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/DatabaseDriver.cs#L3-L60)</sub>

```diff
@@ -1,15 +1,62 @@
 using Microsoft.EntityFrameworkCore;
 
+using MySqlConnector;
+
 using WIMP.App.Data.Db;
 
 namespace WIMP.Specs.Drivers;
 
 public class DatabaseDriver(IDbContextFactory<WimpDbContext> dbContextFactory) : IDatabaseDriver
 {
-    public async Task RecreateDatabase()
+    public async Task EmptyDatabase()
+    {
+        var tablesToEmpty = GetAllTables();
+        await EmptyTables(tablesToEmpty);
+    }
+
+    private async Task EmptyTables(IEnumerable<string> tables)
     {
         await using var db = await dbContextFactory.CreateDbContextAsync();
-        await db.Database.EnsureDeletedAsync();
-        await db.Database.MigrateAsync(); // this call ensures the creation of the database
+        await using var connection = (MySqlConnection)db.Database.GetDbConnection();
+        await connection.OpenAsync();
+
+        await using (var fkCheckOff = new MySqlCommand("SET FOREIGN_KEY_CHECKS = 0;", connection))
+        {
+            await fkCheckOff.ExecuteNonQueryAsync();
+        }
+
+        try
+        {
+            foreach (string table in tables)
+            {
+                await using var cmd = new MySqlCommand($"TRUNCATE TABLE {table};", connection);
+                await cmd.ExecuteNonQueryAsync();
+            }
+        }
+        finally
+        {
+            await using var fkCheckOn = new MySqlCommand("SET FOREIGN_KEY_CHECKS = 1;", connection);
+            await fkCheckOn.ExecuteNonQueryAsync();
+        }
+    }
+
+    private IEnumerable<string> GetAllTables()
+    {
+        using var db = dbContextFactory.CreateDbContext();
+        return db.Model.GetEntityTypes()
+            .Select(et => et.GetTableName()!)
+            .Where(t => t != "__EFMigrationsHistory");
+    }
+
+    private static bool databaseInitialized = false;
+
+    public async Task UpgradeSchemaIfNeeded()
+    {
+        if (!databaseInitialized)
+        {
+            await using var db = await dbContextFactory.CreateDbContextAsync();
+            await db.Database.MigrateAsync();
+            databaseInitialized = true;
+        }
     }
 }
```

### WIMP.Specs/Drivers/IDatabaseDriver.cs

[View file](After/WIMP.Specs/Drivers/IDatabaseDriver.cs#L2)

<sub>[Jump to change](After/WIMP.Specs/Drivers/IDatabaseDriver.cs#L5-L6)</sub>

```diff
@@ -2,5 +2,6 @@ namespace WIMP.Specs.Drivers;
 
 public interface IDatabaseDriver
 {
-    public Task RecreateDatabase();
+    public Task EmptyDatabase();
+    public Task UpgradeSchemaIfNeeded();
 }
```

### WIMP.Specs/Drivers/StubDatabaseDriver.cs

[View file](After/WIMP.Specs/Drivers/StubDatabaseDriver.cs#L2)

<sub>[Jump to change](After/WIMP.Specs/Drivers/StubDatabaseDriver.cs#L5-L11)</sub>

```diff
@@ -2,7 +2,13 @@ namespace WIMP.Specs.Drivers;
 
 public class StubDatabaseDriver : IDatabaseDriver
 {
-    public Task RecreateDatabase()
+    public Task EmptyDatabase()
+    {
+        //nop
+        return Task.CompletedTask;
+    }
+
+    public Task UpgradeSchemaIfNeeded()
     {
         //nop
         return Task.CompletedTask;
```

### WIMP.Specs/Support/Hooks.cs

[View file](After/WIMP.Specs/Support/Hooks.cs#L11)

<sub>[Jump to change](After/WIMP.Specs/Support/Hooks.cs#L14-L15)</sub>

```diff
@@ -11,7 +11,8 @@ public class Hooks(AppHostingContext appHostingContext, IDatabaseDriver database
     [BeforeScenario(Order = 0)]
     public async Task ResetDatabase()
     {
-        await databaseDriver.RecreateDatabase();
+        await databaseDriver.UpgradeSchemaIfNeeded();
+        await databaseDriver.EmptyDatabase();
     }
 
     [BeforeScenario(Order = 1)]
```
