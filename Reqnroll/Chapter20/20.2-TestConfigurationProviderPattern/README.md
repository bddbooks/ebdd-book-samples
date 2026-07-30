# Pattern Differences: 20.2-TestConfigurationProviderPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Drivers/PaymentGatewaySimulator.cs](#wimpspecsdriverspaymentgatewaysimulatorcs)
- ➕ Added [WIMP.Specs/Support/TestConfigurationProvider.cs](#wimpspecssupporttestconfigurationprovidercs)
- 📝 Modified [WIMP.Specs/WIMP.Specs.csproj](#wimpspecswimpspecscsproj)
- ➕ Added [WIMP.Specs/testconfig.json](#wimpspecstestconfigjson)

## Detailed Changes

### WIMP.Specs/Drivers/PaymentGatewaySimulator.cs

[View file](After/WIMP.Specs/Drivers/PaymentGatewaySimulator.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/PaymentGatewaySimulator.cs#L3-L12)</sub>

```diff
@@ -1,14 +1,15 @@
 using WIMP.App.Models;
 using WIMP.App.Services;
+using WIMP.Specs.Support;
 
 namespace WIMP.Specs.Drivers;
 
-public class PaymentGatewaySimulator
+public class PaymentGatewaySimulator(TestConfigurationProvider testConfigurationProvider)
 {
     public SimulatedPaymentGateway Start()
     {
-        string url = "http://localhost/pgwsim";
-        string apiKey = "39845yjkhsd8ke";
+        string url = testConfigurationProvider.PaymentGateway.Url;
+        string apiKey = testConfigurationProvider.PaymentGateway.ApiKey;
 
         return new SimulatedPaymentGateway(url, apiKey);
     }
```

### WIMP.Specs/Support/TestConfigurationProvider.cs

[View file](After/WIMP.Specs/Support/TestConfigurationProvider.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/TestConfigurationProvider.cs#L1-L56)</sub>

```diff
@@ -0,0 +1,56 @@
+using Microsoft.Extensions.Configuration;
+
+namespace WIMP.Specs.Support;
+
+public class TestConfigurationProvider
+{
+    public class DatabaseConfiguration
+    {
+        public bool UseStub { get; set; } = true;
+        public string ConnectionString { get; set; } = null!;
+    }
+
+    public class PaymentGatewayConfiguration
+    {
+        public string Url { get; set; } = null!;
+        public string ApiKey { get; set; } = null!;
+    }
+
+
+    private readonly IConfigurationRoot configurationRoot =
+        new ConfigurationBuilder()
+            .SetBasePath(AppContext.BaseDirectory)
+            .AddJsonFile("testconfig.json", optional: true)
+            .AddEnvironmentVariables(prefix: "WIMP__")
+            .Build();
+
+
+    public PaymentGatewayConfiguration PaymentGateway
+    {
+        get
+        {
+            var paymentGatewayConfig = configurationRoot
+                .GetRequiredSection("PaymentGateway")
+                .Get<PaymentGatewayConfiguration>()!;
+            return paymentGatewayConfig.Url is null
+                ? throw new InvalidOperationException("Missing configuration setting: PaymentGateway:Url")
+                : paymentGatewayConfig.ApiKey is null
+                    ? throw new InvalidOperationException("Missing configuration setting: PaymentGateway:ApiKey")
+                    : paymentGatewayConfig;
+        }
+    }
+
+    public DatabaseConfiguration Database
+    {
+        get
+        {
+            var databaseConfig = configurationRoot
+                .GetRequiredSection("Database")
+                .Get<DatabaseConfiguration>()!;
+            return databaseConfig.ConnectionString is null
+                ? throw new InvalidOperationException("Missing configuration setting: Database:ConnectionString")
+                : databaseConfig;
+        }
+    }
+}
+
```

### WIMP.Specs/WIMP.Specs.csproj

[View file](After/WIMP.Specs/WIMP.Specs.csproj#L27)

<sub>[Jump to change](After/WIMP.Specs/WIMP.Specs.csproj#L30-L35)</sub>

```diff
@@ -27,4 +27,10 @@
     <PackageReference Include="Reqnroll.MsTest" Version="3.3.3" />
   </ItemGroup>
 
+  <ItemGroup>
+    <None Update="testconfig.json">
+      <CopyToOutputDirectory>PreserveNewest</CopyToOutputDirectory>
+    </None>
+  </ItemGroup>
+
 </Project>
```

### WIMP.Specs/testconfig.json

[View file](After/WIMP.Specs/testconfig.json#L1)

<sub>[Jump to change](After/WIMP.Specs/testconfig.json#L1-L10)</sub>

```diff
@@ -0,0 +1,10 @@
+{
+  "Database": { // not used yet
+    "UseStub": true,
+    "ConnectionString": "Server=localhost;Port=3306;Database=wimp_test_db;User=root;Password=root;"
+  },
+  "PaymentGateway": {
+    "Url": "http://localhost/pgwsim",
+    "ApiKey": "39845yjkhsd8ke"
+  }
+}
```
