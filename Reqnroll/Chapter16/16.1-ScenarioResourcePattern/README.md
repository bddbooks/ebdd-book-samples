# Pattern Differences: 16.1-ScenarioResourcePattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Features/Payments.feature](#wimpspecsfeaturespaymentsfeature)
- 📝 Modified [WIMP.Specs/Support/Hooks.cs](#wimpspecssupporthookscs)
- 📝 Modified [WIMP.Specs/Support/WimpAppHost.cs](#wimpspecssupportwimpapphostcs)

## Detailed Changes

### WIMP.Specs/Features/Payments.feature

[View file](After/WIMP.Specs/Features/Payments.feature#L2)

<sub>[Jump to change](After/WIMP.Specs/Features/Payments.feature#L5)</sub>

```diff
@@ -2,6 +2,7 @@
 
 Rule: Card payments must be authorised by payment gateway
 
+  @payment_gateway
   Scenario: Customer is shown payment reference
     Given an authenticated customer has placed an order
     When their payment is authorised by the payment gateway
```

### WIMP.Specs/Support/Hooks.cs

[View file](After/WIMP.Specs/Support/Hooks.cs#L7)

<sub>[Jump to change](After/WIMP.Specs/Support/Hooks.cs#L10-L18)</sub>

```diff
@@ -7,10 +7,15 @@ namespace WIMP.Specs.Support;
 [Binding]
 public class Hooks(AppHostingContext appHostingContext)
 {
-    [BeforeScenario]
-    public void CreateAppHost()
+    [BeforeScenario("@payment_gateway", Order = 1)]
+    public void InitializePaymentGateway()
     {
         appHostingContext.PaymentGateway = PaymentGatewaySimulator.Start();
+    }
+
+    [BeforeScenario(Order = 2)]
+    public void CreateAppHost()
+    {
         appHostingContext.AppHost = new WimpAppHost(appHostingContext.PaymentGateway);
     }
 
```

<sub>[Jump to change](After/WIMP.Specs/Support/Hooks.cs#L28)</sub>

```diff
@@ -20,7 +25,7 @@ public class Hooks(AppHostingContext appHostingContext)
         appHostingContext.AppHost.Dispose();
     }
 
-    [AfterScenario]
+    [AfterScenario("@payment_gateway")]
     public void DisposePaymentGateway()
     {
         appHostingContext.PaymentGateway?.Stop();
```

### WIMP.Specs/Support/WimpAppHost.cs

[View file](After/WIMP.Specs/Support/WimpAppHost.cs#L9)

<sub>[Jump to change](After/WIMP.Specs/Support/WimpAppHost.cs#L12)</sub>

```diff
@@ -9,7 +9,7 @@ using WIMP.App.Services;
 
 namespace WIMP.Specs.Support;
 
-public class WimpAppHost(IPaymentGateway paymentGateway) : WebApplicationFactory<Program>
+public class WimpAppHost(IPaymentGateway? customPaymentGateway) : WebApplicationFactory<Program>
 {
     protected override void ConfigureWebHost(IWebHostBuilder builder)
     {
```

<sub>[Jump to change](After/WIMP.Specs/Support/WimpAppHost.cs#L23-L27)</sub>

```diff
@@ -20,7 +20,10 @@ public class WimpAppHost(IPaymentGateway paymentGateway) : WebApplicationFactory
             loggingBuilder.ClearProviders();
             loggingBuilder.Services.TryAddEnumerable(ServiceDescriptor.Singleton<ILoggerProvider, DebugLoggerProvider>(_ => new DebugLoggerProvider()));
         });
-        builder.ConfigureServices(diConfig =>
-            diConfig.AddSingleton(paymentGateway));
+        if (customPaymentGateway != null)
+        {
+            builder.ConfigureServices(diConfig =>
+                diConfig.AddSingleton<IPaymentGateway>(customPaymentGateway));
+        }
     }
 }
```
