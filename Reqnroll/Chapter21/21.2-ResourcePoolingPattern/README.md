# Pattern Differences: 21.2-ResourcePoolingPattern

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

- ➕ Added [WIMP.Specs/Drivers/DatabasePoolDriver.cs](#wimpspecsdriversdatabasepooldrivercs)
- 📝 Modified [WIMP.Specs/MSTestSettings.cs](#wimpspecsmstestsettingscs)
- ➕ Added [WIMP.Specs/Support/DatabaseContext.cs](#wimpspecssupportdatabasecontextcs)
- 📝 Modified [WIMP.Specs/Support/DiConfiguration.cs](#wimpspecssupportdiconfigurationcs)
- 📝 Modified [WIMP.Specs/Support/Hooks.cs](#wimpspecssupporthookscs)
- 📝 Modified [WIMP.Specs/Support/TestConfigurationProvider.cs](#wimpspecssupporttestconfigurationprovidercs)
- 📝 Modified [WIMP.Specs/testconfig.json](#wimpspecstestconfigjson)

## Detailed Changes

### WIMP.Specs/Drivers/DatabasePoolDriver.cs

[View file](After/WIMP.Specs/Drivers/DatabasePoolDriver.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/DatabasePoolDriver.cs#L1-L90)</sub>

```diff
@@ -0,0 +1,90 @@
+using System.Collections.Concurrent;
+
+using Microsoft.Extensions.ObjectPool;
+
+using WIMP.App.Data.Db;
+using WIMP.Specs.Support;
+
+namespace WIMP.Specs.Drivers;
+
+public static class DatabasePoolDriver
+{
+    private static readonly string[] connectionStrings = GetConnectionStrings();
+
+    private static readonly TimeSpan acquireTimeout = TimeSpan.FromSeconds(10);
+
+    private static readonly ObjectPool<DatabaseContext> databasePool =
+        new DefaultObjectPoolProvider
+        {
+            MaximumRetained = connectionStrings.Length
+        }.Create(new DatabaseContextPooledObjectPolicy(connectionStrings));
+
+    private static readonly SemaphoreSlim availableDatabaseSignal =
+        new(initialCount: connectionStrings.Length, maxCount: connectionStrings.Length);
+
+    private static string[] GetConnectionStrings()
+    {
+        var testConfigurationProvider = new TestConfigurationProvider();
+        return Enumerable.Range(0, testConfigurationProvider.Database.PoolSize).Select(i =>
+                testConfigurationProvider.Database.PooledConnectionStringTemplate.Replace("{index}", i.ToString()))
+            .ToArray();
+    }
+
+    public static DatabaseContext AcquireDatabase()
+    {
+        if (!availableDatabaseSignal.Wait(acquireTimeout))
+        {
+            throw new TimeoutException(
+                $"Timed out after {acquireTimeout.TotalSeconds:0} seconds while waiting for a pooled database context.");
+        }
+
+        try
+        {
+            var databaseContext = databasePool.Get();
+            Console.WriteLine($"Using DB from pool: {databaseContext.DbContextFactory}");
+            return databaseContext.TryLease() ? databaseContext :
+                throw new InvalidOperationException("Internal error: acquired a database context that is already leased.");
+        }
+        catch
+        {
+            availableDatabaseSignal.Release();
+            throw;
+        }
+    }
+
+    public static void ReleaseDatabase(DatabaseContext databaseContext)
+    {
+        ArgumentNullException.ThrowIfNull(databaseContext);
+
+        if (!databaseContext.TryRelease())
+        {
+            throw new InvalidOperationException("Attempted to release a database context that is not currently leased.");
+        }
+
+        databasePool.Return(databaseContext);
+        availableDatabaseSignal.Release();
+    }
+
+    private sealed class DatabaseContextPooledObjectPolicy(string[] connectionStrings)
+        : PooledObjectPolicy<DatabaseContext>
+    {
+        private readonly ConcurrentQueue<string> remainingConnectionStrings = new(connectionStrings);
+
+        public override DatabaseContext Create()
+        {
+            if (!remainingConnectionStrings.TryDequeue(out string? connectionString))
+            {
+                throw new InvalidOperationException(
+                    "Internal error: attempted to create more pooled database contexts than configured.");
+            }
+
+            Console.WriteLine($"Initializing pooled DB for {connectionString}");
+            var dbContextFactory = WimpDbContextOptionsBuilder.CreateDbContextFactory(connectionString);
+            var databaseContext = new DatabaseContext(dbContextFactory);
+            DatabaseDriver.UpgradeSchemaIfNeeded(databaseContext.DbContextFactory);
+            return databaseContext;
+        }
+
+        public override bool Return(DatabaseContext obj) => true;
+    }
+}
```

### WIMP.Specs/MSTestSettings.cs

[View file](After/WIMP.Specs/MSTestSettings.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/MSTestSettings.cs#L1)</sub>

```diff
@@ -1 +1 @@
-[assembly: DoNotParallelize]
+[assembly: Parallelize(Scope = ExecutionScope.MethodLevel, Workers = 4)]
```

### WIMP.Specs/Support/DatabaseContext.cs

[View file](After/WIMP.Specs/Support/DatabaseContext.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/DatabaseContext.cs#L1-L22)</sub>

```diff
@@ -0,0 +1,22 @@
+using Microsoft.EntityFrameworkCore;
+
+using WIMP.App.Data.Db;
+using WIMP.Specs.Drivers;
+
+namespace WIMP.Specs.Support;
+
+public class DatabaseContext(IDbContextFactory<WimpDbContext> dbContextFactory) : IDisposable
+{
+    public IDbContextFactory<WimpDbContext> DbContextFactory => dbContextFactory;
+
+    private int isLeased;
+
+    internal bool TryLease() => Interlocked.Exchange(ref isLeased, 1) == 0;
+
+    internal bool TryRelease() => Interlocked.Exchange(ref isLeased, 0) == 1;
+
+    public void Dispose()
+    {
+        DatabasePoolDriver.ReleaseDatabase(this);
+    }
+}
```

### WIMP.Specs/Support/DiConfiguration.cs

[View file](After/WIMP.Specs/Support/DiConfiguration.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/DiConfiguration.cs#L1-L2)</sub>

```diff
@@ -1,3 +1,5 @@
+using Microsoft.EntityFrameworkCore;
+
 using Reqnroll;
 using Reqnroll.BoDi;
 
```

<sub>[Jump to change](After/WIMP.Specs/Support/DiConfiguration.cs#L29-L31)</sub>

```diff
@@ -24,8 +26,9 @@ public class DiConfiguration(TestConfigurationProvider testConfigurationProvider
             Console.WriteLine("Using real database");
             scenarioContainer.RegisterTypeAs<DatabaseDriver, IDatabaseDriver>();
             scenarioContainer.RegisterTypeAs<DataRepository, IDataRepository>();
-            string connectionString = testConfigurationProvider.Database.ConnectionString;
-            scenarioContainer.RegisterInstanceAs(WimpDbContextOptionsBuilder.CreateDbContextFactory(connectionString));
+            // get database context factory from pool
+            scenarioContainer.RegisterFactoryAs(DatabasePoolDriver.AcquireDatabase);
+            scenarioContainer.RegisterFactoryAs<IDbContextFactory<WimpDbContext>>(di => di.Resolve<DatabaseContext>().DbContextFactory);
         }
     }
 }
```

### WIMP.Specs/Support/Hooks.cs

[View file](After/WIMP.Specs/Support/Hooks.cs#L1)

```diff
@@ -1,7 +1,6 @@
 using Reqnroll;
 
 using WIMP.App.Data;
-using WIMP.App.Data.Db;
 using WIMP.Specs.Drivers;
 
 namespace WIMP.Specs.Support;
```

```diff
@@ -9,17 +8,6 @@ namespace WIMP.Specs.Support;
 [Binding]
 public class Hooks(AppHostingContext appHostingContext, IDatabaseDriver databaseDriver, IDataRepository dataRepository, MenuBackdoorDriver menuBackdoorDriver)
 {
-    [BeforeTestRun]
-    public static void InitializeDatabase()
-    {
-        var testConfigurationProvider = new TestConfigurationProvider();
-        if (!testConfigurationProvider.Database.UseStub)
-        {
-            var dbContextFactory = WimpDbContextOptionsBuilder.CreateDbContextFactory(testConfigurationProvider.Database.ConnectionString);
-            DatabaseDriver.UpgradeSchemaIfNeeded(dbContextFactory);
-        }
-    }
-
     [BeforeScenario(Order = 0)]
     public async Task ResetDatabase()
     {
```

### WIMP.Specs/Support/TestConfigurationProvider.cs

[View file](After/WIMP.Specs/Support/TestConfigurationProvider.cs#L8)

<sub>[Jump to change](After/WIMP.Specs/Support/TestConfigurationProvider.cs#L11-L12)</sub>

```diff
@@ -8,6 +8,8 @@ public class TestConfigurationProvider
     {
         public bool UseStub { get; set; } = true;
         public string ConnectionString { get; set; } = null!;
+        public int PoolSize { get; set; }
+        public string PooledConnectionStringTemplate { get; set; } = null!;
     }
 
     public class PaymentGatewayConfiguration
```

### WIMP.Specs/testconfig.json

[View file](After/WIMP.Specs/testconfig.json#L1)

<sub>[Jump to change](After/WIMP.Specs/testconfig.json#L4-L6)</sub>

```diff
@@ -1,7 +1,9 @@
 {
   "Database": {
     "UseStub": false,
-    "ConnectionString": "Server=localhost;Port=3306;Database=wimp_test_db;User=root;Password=root;"
+    "ConnectionString": "Server=localhost;Port=3306;Database=wimp_test_db;User=root;Password=root;",
+    "PoolSize": 4,
+    "PooledConnectionStringTemplate": "Server=localhost;Port=3306;Database=wimp_test_db_{index};User=root;Password=root;"
   },
   "PaymentGateway": {
     "Url": "http://localhost/pgwsim",
```
