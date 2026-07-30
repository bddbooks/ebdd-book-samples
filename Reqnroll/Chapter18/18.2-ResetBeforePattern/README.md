# Pattern Differences: 18.2-ResetBeforePattern

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

[View file](After/WIMP.Specs/Drivers/DatabaseDriver.cs#L6)

<sub>[Jump to change](After/WIMP.Specs/Drivers/DatabaseDriver.cs#L9-L13)</sub>

```diff
@@ -6,15 +6,10 @@ namespace WIMP.Specs.Drivers;
 
 public class DatabaseDriver(IDbContextFactory<WimpDbContext> dbContextFactory) : IDatabaseDriver
 {
-    public async Task CreateDatabase()
-    {
-        await using var db = await dbContextFactory.CreateDbContextAsync();
-        await db.Database.MigrateAsync(); // this call ensures the creation of the database
-    }
-
-    public async Task DropDatabase()
+    public async Task RecreateDatabase()
     {
         await using var db = await dbContextFactory.CreateDbContextAsync();
         await db.Database.EnsureDeletedAsync();
+        await db.Database.MigrateAsync(); // this call ensures the creation of the database
     }
 }
```

### WIMP.Specs/Drivers/IDatabaseDriver.cs

[View file](After/WIMP.Specs/Drivers/IDatabaseDriver.cs#L2)

<sub>[Jump to change](After/WIMP.Specs/Drivers/IDatabaseDriver.cs#L5)</sub>

```diff
@@ -2,6 +2,5 @@ namespace WIMP.Specs.Drivers;
 
 public interface IDatabaseDriver
 {
-    public Task CreateDatabase();
-    public Task DropDatabase();
+    public Task RecreateDatabase();
 }
```

### WIMP.Specs/Drivers/StubDatabaseDriver.cs

[View file](After/WIMP.Specs/Drivers/StubDatabaseDriver.cs#L2)

<sub>[Jump to change](After/WIMP.Specs/Drivers/StubDatabaseDriver.cs#L5)</sub>

```diff
@@ -2,13 +2,7 @@ namespace WIMP.Specs.Drivers;
 
 public class StubDatabaseDriver : IDatabaseDriver
 {
-    public Task CreateDatabase()
-    {
-        //nop
-        return Task.CompletedTask;
-    }
-
-    public Task DropDatabase()
+    public Task RecreateDatabase()
     {
         //nop
         return Task.CompletedTask;
```

### WIMP.Specs/Support/Hooks.cs

[View file](After/WIMP.Specs/Support/Hooks.cs#L9)

<sub>[Jump to change](After/WIMP.Specs/Support/Hooks.cs#L12-L14)</sub>

```diff
@@ -9,15 +9,9 @@ namespace WIMP.Specs.Support;
 public class Hooks(AppHostingContext appHostingContext, IDatabaseDriver databaseDriver, IDataRepository dataRepository)
 {
     [BeforeScenario(Order = 0)]
-    public async Task CreateDatabase()
+    public async Task ResetDatabase()
     {
-        await databaseDriver.CreateDatabase();
-    }
-
-    [AfterScenario]
-    public async Task DropDatabase()
-    {
-        await databaseDriver.DropDatabase();
+        await databaseDriver.RecreateDatabase();
     }
 
     [BeforeScenario(Order = 1)]
```
