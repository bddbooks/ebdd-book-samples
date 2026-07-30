# Pattern Differences: 21.1-TestEnvironmentInitializationPattern

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
- 📝 Modified [WIMP.Specs/Support/Hooks.cs](#wimpspecssupporthookscs)

## Detailed Changes

### WIMP.Specs/Drivers/DatabaseDriver.cs

[View file](After/WIMP.Specs/Drivers/DatabaseDriver.cs#L3)

<sub>[Jump to change](After/WIMP.Specs/Drivers/DatabaseDriver.cs#L9)</sub>

```diff
@@ -3,20 +3,11 @@ using Microsoft.EntityFrameworkCore;
 using MySqlConnector;
 
 using WIMP.App.Data.Db;
-using WIMP.Specs.Support;
 
 namespace WIMP.Specs.Drivers;
 
-public class DatabaseDriver : IDatabaseDriver
+public class DatabaseDriver(IDbContextFactory<WimpDbContext> dbContextFactory) : IDatabaseDriver
 {
-    private readonly IDbContextFactory<WimpDbContext> dbContextFactory;
-
-    public DatabaseDriver(IDbContextFactory<WimpDbContext> dbContextFactory)
-    {
-        this.dbContextFactory = dbContextFactory;
-        EnsureInitialized();
-    }
-
     public async Task EmptyDatabase()
     {
         var tablesToEmpty = GetAllTables();
```

```diff
@@ -57,20 +48,6 @@ public class DatabaseDriver : IDatabaseDriver
             .Where(t => t != "__EFMigrationsHistory");
     }
 
-    private static volatile bool initialized = false;
-
-    private static void EnsureInitialized()
-    {
-        if (initialized)
-        {
-            return;
-        }
-
-        var testConfigurationProvider = new TestConfigurationProvider();
-        UpgradeSchemaIfNeeded(testConfigurationProvider.Database.ConnectionString);
-        initialized = true;
-    }
-
     public static void UpgradeSchemaIfNeeded(string connectionString)
     {
         var dbContextFactory = WimpDbContextOptionsBuilder.CreateDbContextFactory(connectionString);
```

### WIMP.Specs/Support/Hooks.cs

[View file](After/WIMP.Specs/Support/Hooks.cs#L8)

<sub>[Jump to change](After/WIMP.Specs/Support/Hooks.cs#L11-L20)</sub>

```diff
@@ -8,6 +8,16 @@ namespace WIMP.Specs.Support;
 [Binding]
 public class Hooks(AppHostingContext appHostingContext, IDatabaseDriver databaseDriver, IDataRepository dataRepository, MenuBackdoorDriver menuBackdoorDriver)
 {
+    [BeforeTestRun]
+    public static void InitializeDatabase()
+    {
+        var testConfigurationProvider = new TestConfigurationProvider();
+        if (!testConfigurationProvider.Database.UseStub)
+        {
+            DatabaseDriver.UpgradeSchemaIfNeeded(testConfigurationProvider.Database.ConnectionString);
+        }
+    }
+
     [BeforeScenario(Order = 0)]
     public async Task ResetDatabase()
     {
```
