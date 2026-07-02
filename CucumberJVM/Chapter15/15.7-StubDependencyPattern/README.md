# Pattern Differences: 15.7-StubDependencyPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- ➕ Added [src/test/java/com/wimp/specs/drivers/TimeServiceDriver.java](#srctestjavacomwimpspecsdriverstimeservicedriverjava)
- 📝 Modified [src/test/java/com/wimp/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpspecsstepdefinitionsorderingstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/specs/support/CucumberSpringConfiguration.java](#srctestjavacomwimpspecssupportcucumberspringconfigurationjava)
- ➕ Added [src/test/java/com/wimp/specs/support/StubTimeService.java](#srctestjavacomwimpspecssupportstubtimeservicejava)

## Detailed Changes

### src/test/java/com/wimp/specs/drivers/TimeServiceDriver.java

[View file](After/src/test/java/com/wimp/specs/drivers/TimeServiceDriver.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/specs/drivers/TimeServiceDriver.java#L1-L25)</sub>

```diff
@@ -0,0 +1,25 @@
+package com.wimp.specs.drivers;
+
+import com.wimp.specs.support.StubTimeService;
+import org.springframework.stereotype.Component;
+
+import java.time.LocalDateTime;
+import java.time.LocalTime;
+
+@Component
+public class TimeServiceDriver {
+    private final StubTimeService stubTimeService;
+
+    public TimeServiceDriver(StubTimeService stubTimeService) {
+        this.stubTimeService = stubTimeService;
+    }
+
+    public void setCurrentTime(LocalTime time) {
+        LocalDateTime dateTime = getTodayTime(time);
+        stubTimeService.setCurrentTime(dateTime);
+    }
+
+    public LocalDateTime getTodayTime(LocalTime time) {
+        return stubTimeService.getCurrentTime().with(time);
+    }
+}
```

### src/test/java/com/wimp/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/specs/stepdefinitions/OrderingStepDefinitions.java#L4)

<sub>[Jump to change](After/src/test/java/com/wimp/specs/stepdefinitions/OrderingStepDefinitions.java#L7)</sub>

```diff
@@ -4,6 +4,7 @@ import com.wimp.app.models.Notification;
 import com.wimp.app.restapi.PlaceOrderRequest;
 import com.wimp.specs.drivers.NotificationsApiDriver;
 import com.wimp.specs.drivers.OrderingApiDriver;
+import com.wimp.specs.drivers.TimeServiceDriver;
 import com.wimp.specs.support.DomainDefaults;
 import com.wimp.specs.support.PlaceOrderRequestObjectMother;
 import io.cucumber.datatable.DataTable;
```

```diff
@@ -12,7 +13,6 @@ import io.cucumber.java.en.Then;
 import io.cucumber.java.en.When;
 import org.springframework.beans.factory.annotation.Autowired;
 
-import java.time.LocalDateTime;
 import java.time.LocalTime;
 import java.util.List;
 import java.util.Map;
```

<sub>[Jump to change](After/src/test/java/com/wimp/specs/stepdefinitions/OrderingStepDefinitions.java#L23-L33)</sub>

```diff
@@ -20,11 +20,17 @@ import java.util.Map;
 import static org.junit.jupiter.api.Assertions.assertTrue;
 
 public class OrderingStepDefinitions {
+    private final TimeServiceDriver timeServiceDriver;
     private final OrderingApiDriver orderingApiDriver;
     private final NotificationsApiDriver notificationsApiDriver;
 
     @Autowired
-    public OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, NotificationsApiDriver notificationsApiDriver) {
+    public OrderingStepDefinitions(
+        TimeServiceDriver timeServiceDriver,
+        OrderingApiDriver orderingApiDriver,
+        NotificationsApiDriver notificationsApiDriver
+    ) {
+        this.timeServiceDriver = timeServiceDriver;
         this.orderingApiDriver = orderingApiDriver;
         this.notificationsApiDriver = notificationsApiDriver;
     }
```

<sub>[Jump to change](After/src/test/java/com/wimp/specs/stepdefinitions/OrderingStepDefinitions.java#L43-L55)</sub>

```diff
@@ -34,21 +40,19 @@ public class OrderingStepDefinitions {
         List<Map<String, String>> rows = dataTable.asMaps();
         LocalTime expectedDeliveryTime = LocalTime.parse(rows.getFirst().get("expected delivery time"));
 
-        // With the real time service we cannot fast-forward time, so cannot use the specified
-        // expectedDeliveryTime. Instead, we force the expected delivery time being in 0.5 seconds,
-        // and we wait in the whenTheDeliveryHasNotBeenMadeBy method for the background timer loop
-        // to process the subscription.
+        // ensuring that the placing time is before the expected delivery time
+        timeServiceDriver.setCurrentTime(expectedDeliveryTime.minusMinutes(5));
+        // preparing a place order request with expected delivery time (this setting is only available for testing)
         PlaceOrderRequest placeOrderRequest = new PlaceOrderRequestObjectMother()
-            .withExpectedDeliveryTime(LocalDateTime.now().plusNanos(500_000_000))
+            .withExpectedDeliveryTime(timeServiceDriver.getTodayTime(expectedDeliveryTime))
             .build();
 
         orderingApiDriver.placeOrder(placeOrderRequest).execute();
     }
 
     @When("^the delivery has not been made by (\\d{2}:\\d{2})$")
-    public void whenTheDeliveryHasNotBeenMadeBy(String time) throws InterruptedException {
-        // Workaround: see notes above!
-        Thread.sleep(2_000);
+    public void whenTheDeliveryHasNotBeenMadeBy(String time) {
+        timeServiceDriver.setCurrentTime(LocalTime.parse(time));
     }
 
     @Then("the customer should receive a notification about the delay")
```

### src/test/java/com/wimp/specs/support/CucumberSpringConfiguration.java

[View file](After/src/test/java/com/wimp/specs/support/CucumberSpringConfiguration.java#L16)

<sub>[Jump to change](After/src/test/java/com/wimp/specs/support/CucumberSpringConfiguration.java#L19-L20)</sub>

```diff
@@ -16,7 +16,8 @@ import org.springframework.web.context.WebApplicationContext;
 @CucumberContextConfiguration
 @SpringBootTest(classes = WimpApplication.class)
 @Import({
-    CucumberSpringConfiguration.SpecsConfiguration.class})
+    CucumberSpringConfiguration.SpecsConfiguration.class,
+    CucumberSpringConfiguration.StubDependencyConfiguration.class})
 public class CucumberSpringConfiguration {
     @TestConfiguration
     @ComponentScan("com.wimp.specs")
```

<sub>[Jump to change](After/src/test/java/com/wimp/specs/support/CucumberSpringConfiguration.java#L30-L43)</sub>

```diff
@@ -26,4 +27,18 @@ public class CucumberSpringConfiguration {
             return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
         }
     }
+
+    @TestConfiguration
+    static class StubDependencyConfiguration {
+        @Bean
+        public StubTimeService stubTimeService() {
+            return new StubTimeService();
+        }
+
+        @Bean
+        @Primary
+        public TimeService timeService(StubTimeService stubTimeService) {
+            return stubTimeService;
+        }
+    }
 }
```

### src/test/java/com/wimp/specs/support/StubTimeService.java

[View file](After/src/test/java/com/wimp/specs/support/StubTimeService.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/specs/support/StubTimeService.java#L1-L37)</sub>

```diff
@@ -0,0 +1,37 @@
+package com.wimp.specs.support;
+
+import com.wimp.app.services.TimeService;
+
+import java.time.LocalDateTime;
+import java.util.ArrayList;
+import java.util.List;
+import java.util.function.Predicate;
+
+public class StubTimeService implements TimeService {
+    private LocalDateTime now = LocalDateTime.now();
+    private final List<Predicate<LocalDateTime>> timeChangeSubscribers = new ArrayList<>();
+
+    @Override
+    public LocalDateTime getCurrentTime() {
+        return now;
+    }
+
+    @Override
+    public void subscribeToTimeChange(Predicate<LocalDateTime> onTimeChanged) {
+        timeChangeSubscribers.add(onTimeChanged);
+    }
+
+    public void setCurrentTime(LocalDateTime currentDateTime) {
+        now = currentDateTime;
+        triggerTimeChange();
+    }
+
+    public void triggerTimeChange() {
+        LocalDateTime currentDateTime = getCurrentTime();
+        for (Predicate<LocalDateTime> subscriber : List.copyOf(timeChangeSubscribers)) {
+            if (subscriber.test(currentDateTime)) {
+                timeChangeSubscribers.remove(subscriber);
+            }
+        }
+    }
+}
```
