# Pattern Differences: 20.3-ParameterizedExecutionPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

The sample uses stub database by default. If you wish to try it with real database (by changing `Database.UseStub` in `testconfig.json`), you need to setup a MySQL database.

The following instructions are for setting up a MySQL database in a Docker container.

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

- 📝 Modified [WIMP.Specs/Support/DiConfiguration.cs](#wimpspecssupportdiconfigurationcs)
- 📝 Modified [WIMP.Specs/Support/TestConfigurationProvider.cs](#wimpspecssupporttestconfigurationprovidercs)
- 📝 Modified [WIMP.Specs/testconfig.json](#wimpspecstestconfigjson)

## Detailed Changes

### WIMP.Specs/Support/DiConfiguration.cs

[View file](After/WIMP.Specs/Support/DiConfiguration.cs#L2)

<sub>[Jump to change](After/WIMP.Specs/Support/DiConfiguration.cs#L5-L29)</sub>

```diff
@@ -2,18 +2,30 @@ using Reqnroll;
 using Reqnroll.BoDi;
 
 using WIMP.App.Data;
+using WIMP.App.Data.Db;
 using WIMP.Specs.Drivers;
 
 namespace WIMP.Specs.Support;
 
 [Binding]
-public class DiConfiguration
+public class DiConfiguration(TestConfigurationProvider testConfigurationProvider)
 {
     [BeforeScenario(Order = -1)]
     public void SetupDependencies(IObjectContainer scenarioContainer)
     {
-        Console.WriteLine("Using stub database");
-        scenarioContainer.RegisterTypeAs<StubDatabaseDriver, IDatabaseDriver>();
-        scenarioContainer.RegisterTypeAs<StubDataRepository, IDataRepository>();
+        if (testConfigurationProvider.Database.UseStub)
+        {
+            Console.WriteLine("Using stub database");
+            scenarioContainer.RegisterTypeAs<StubDatabaseDriver, IDatabaseDriver>();
+            scenarioContainer.RegisterTypeAs<StubDataRepository, IDataRepository>();
+        }
+        else
+        {
+            Console.WriteLine("Using real database");
+            scenarioContainer.RegisterTypeAs<DatabaseDriver, IDatabaseDriver>();
+            scenarioContainer.RegisterTypeAs<DataRepository, IDataRepository>();
+            string connectionString = testConfigurationProvider.Database.ConnectionString;
+            scenarioContainer.RegisterInstanceAs(WimpDbContextOptionsBuilder.CreateDbContextFactory(connectionString));
+        }
     }
 }
```

### WIMP.Specs/Support/TestConfigurationProvider.cs

[View file](After/WIMP.Specs/Support/TestConfigurationProvider.cs#L6)

<sub>[Jump to change](After/WIMP.Specs/Support/TestConfigurationProvider.cs#L9)</sub>

```diff
@@ -6,6 +6,7 @@ public class TestConfigurationProvider
 {
     public class DatabaseConfiguration
     {
+        public bool UseStub { get; set; } = true;
         public string ConnectionString { get; set; } = null!;
     }
 
```

### WIMP.Specs/testconfig.json

[View file](After/WIMP.Specs/testconfig.json#L1)

<sub>[Jump to change](After/WIMP.Specs/testconfig.json#L3)</sub>

```diff
@@ -1,5 +1,6 @@
 {
   "Database": {
+    "UseStub": true,
     "ConnectionString": "Server=localhost;Port=3306;Database=wimp_test_db;User=root;Password=root;"
   },
   "PaymentGateway": {
```
