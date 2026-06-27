# Pattern Differences: 15.7-TheStubDependencyPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- ➕ Added [WIMP.Specs/Drivers/TimeServiceDriver.cs](#wimpspecsdriverstimeservicedrivercs)
- 📝 Modified [WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpspecsstepdefinitionsorderingstepdefinitionscs)
- 📝 Modified [WIMP.Specs/Support/Hooks.cs](#wimpspecssupporthookscs)
- ➕ Added [WIMP.Specs/Support/StubTimeService.cs](#wimpspecssupportstubtimeservicecs)
- 📝 Modified [WIMP.Specs/Support/WimpAppHost.cs](#wimpspecssupportwimpapphostcs)

## Detailed Changes

### WIMP.Specs/Drivers/TimeServiceDriver.cs

[View file](After/WIMP.Specs/Drivers/TimeServiceDriver.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/TimeServiceDriver.cs#L1-L17)</sub>

```diff
@@ -0,0 +1,17 @@
+using WIMP.Specs.Support;
+
+namespace WIMP.Specs.Drivers;
+
+public class TimeServiceDriver(StubTimeService stubTimeService)
+{
+    public void SetCurrentTime(TimeOnly time)
+    {
+        var dateTime = stubTimeService.GetCurrentTime().WithTime(time);
+        stubTimeService.SetCurrentTime(dateTime);
+    }
+
+    public DateTimeOffset GetTodayTime(TimeOnly time)
+    {
+        return stubTimeService.GetCurrentTime().WithTime(time);
+    }
+}
```

### WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L6)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L9)</sub>

```diff
@@ -6,7 +6,7 @@ using WIMP.Specs.Support;
 namespace WIMP.Specs.StepDefinitions;
 
 [Binding]
-public class OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, NotificationsApiDriver notificationsApiDriver)
+public class OrderingStepDefinitions(TimeServiceDriver timeServiceDriver, OrderingApiDriver orderingApiDriver, NotificationsApiDriver notificationsApiDriver)
 {
     public record OrderRequestData(TimeSpan ExpectedDeliveryTime);
 
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L18-L30)</sub>

```diff
@@ -15,23 +15,19 @@ public class OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, Notifi
     {
         var orderData = dataTable.CreateInstance<OrderRequestData>();
         var expectedDeliveryTime = TimeOnly.FromTimeSpan(orderData.ExpectedDeliveryTime);
-
-        // With the real time service we cannot fast-forward time, so cannot use the specified
-        // expectedDeliveryTime. Instead, we force the expected delivery time being in 0.5 seconds,
-        // and we wait in the WhenTheDeliveryHasNotBeenMadeBy method for the background timer loop
-        // to process the subscription.
+        // ensuring that the placing time is before the expected delivery time
+        timeServiceDriver.SetCurrentTime(expectedDeliveryTime.Add(TimeSpan.FromMinutes(-5)));
+        // preparing a place order request with expected delivery time (this setting is only available for testing) 
         var placeOrderRequest = new PlaceOrderRequestObjectMother()
-            .WithExpectedDeliveryTime(DateTimeOffset.Now.AddSeconds(0.5))
+            .WithExpectedDeliveryTime(timeServiceDriver.GetTodayTime(expectedDeliveryTime))
             .Build();
-
         await orderingApiDriver.PlaceOrder(placeOrderRequest).Execute();
     }
 
     [When("the delivery has not been made by {TimeOnly}")]
     public void WhenTheDeliveryHasNotBeenMadeBy(TimeOnly time)
     {
-        //Workaround: see notes above!
-        Thread.Sleep(TimeSpan.FromMilliseconds(1500));
+        timeServiceDriver.SetCurrentTime(time);
     }
 
     [Then("the customer should receive a notification about the delay")]
```

### WIMP.Specs/Support/Hooks.cs

[View file](After/WIMP.Specs/Support/Hooks.cs#L3)

<sub>[Jump to change](After/WIMP.Specs/Support/Hooks.cs#L6-L11)</sub>

```diff
@@ -3,12 +3,12 @@ using Reqnroll;
 namespace WIMP.Specs.Support;
 
 [Binding]
-public class Hooks(AppHostingContext appHostingContext)
+public class Hooks(StubTimeService stubTimeService, AppHostingContext appHostingContext)
 {
     [BeforeScenario]
     public void CreateAppHost()
     {
-        appHostingContext.AppHost = new WimpAppHost();
+        appHostingContext.AppHost = new WimpAppHost(stubTimeService);
     }
 
     [AfterScenario]
```

### WIMP.Specs/Support/StubTimeService.cs

[View file](After/WIMP.Specs/Support/StubTimeService.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/StubTimeService.cs#L1-L32)</sub>

```diff
@@ -0,0 +1,32 @@
+using WIMP.App.Services;
+
+namespace WIMP.Specs.Support;
+
+public class StubTimeService : ITimeService
+{
+    private DateTimeOffset now = DateTimeOffset.Now;
+    private readonly List<Func<DateTimeOffset, bool>> timeChangeSubscribers = [];
+
+    public DateTimeOffset GetCurrentTime() => now;
+
+    public void SubscribeToTimeChange(Func<DateTimeOffset, bool> onTimeChanged) =>
+        timeChangeSubscribers.Add(onTimeChanged);
+
+    public void SetCurrentTime(DateTimeOffset currentDateTime)
+    {
+        now = currentDateTime;
+        TriggerTimeChange();
+    }
+
+    public void TriggerTimeChange()
+    {
+        var currentDateTime = GetCurrentTime();
+        foreach (var subscriber in timeChangeSubscribers.ToArray())
+        {
+            if (subscriber(currentDateTime))
+            {
+                timeChangeSubscribers.Remove(subscriber);
+            }
+        }
+    }
+}
```

### WIMP.Specs/Support/WimpAppHost.cs

[View file](After/WIMP.Specs/Support/WimpAppHost.cs#L5)

<sub>[Jump to change](After/WIMP.Specs/Support/WimpAppHost.cs#L8-L12)</sub>

```diff
@@ -5,9 +5,11 @@ using Microsoft.Extensions.DependencyInjection.Extensions;
 using Microsoft.Extensions.Logging;
 using Microsoft.Extensions.Logging.Debug;
 
+using WIMP.App.Services;
+
 namespace WIMP.Specs.Support;
 
-public class WimpAppHost : WebApplicationFactory<Program>
+public class WimpAppHost(StubTimeService timeService) : WebApplicationFactory<Program>
 {
     protected override void ConfigureWebHost(IWebHostBuilder builder)
     {
```

<sub>[Jump to change](After/WIMP.Specs/Support/WimpAppHost.cs#L23-L24)</sub>

```diff
@@ -18,5 +20,7 @@ public class WimpAppHost : WebApplicationFactory<Program>
             loggingBuilder.ClearProviders();
             loggingBuilder.Services.TryAddEnumerable(ServiceDescriptor.Singleton<ILoggerProvider, DebugLoggerProvider>(_ => new DebugLoggerProvider()));
         });
+        builder.ConfigureServices(diConfig =>
+            diConfig.AddSingleton<ITimeService>(timeService));
     }
 }
```
