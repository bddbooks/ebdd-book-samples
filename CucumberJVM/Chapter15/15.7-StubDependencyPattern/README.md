# Pattern Differences: 15.7-StubDependencyPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/CucumberSpringConfiguration.java](#srctestjavacomwimpappcucumberspringconfigurationjava)
- ➕ Added [src/test/java/com/wimp/app/specs/drivers/TimeServiceDriver.java](#srctestjavacomwimpappspecsdriverstimeservicedriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsorderingstepdefinitionsjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/StubTimeService.java](#srctestjavacomwimpappspecssupportstubtimeservicejava)

## Detailed Changes

### src/test/java/com/wimp/app/CucumberSpringConfiguration.java

[View file](After/src/test/java/com/wimp/app/CucumberSpringConfiguration.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/CucumberSpringConfiguration.java#L4-L7)</sub>

```diff
@@ -1,8 +1,10 @@
 package com.wimp.app;
 
 import com.wimp.app.specs.support.RestApiContext;
+import com.wimp.app.specs.support.StubTimeService;
 
 import io.cucumber.spring.CucumberContextConfiguration;
+import io.cucumber.spring.ScenarioScope;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.boot.test.context.TestConfiguration;
 import org.springframework.context.annotation.Bean;
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/CucumberSpringConfiguration.java#L57-L148)</sub>

```diff
@@ -52,4 +54,96 @@ public class CucumberSpringConfiguration {
                 });
         }
     }
+
+    /**
+     * Configures stub dependencies (see Stub Dependency pattern).
+     * <p>
+     * Stub dependencies in this context are stub implementations of an
+     * application interface that can be used for testing. The stub dependencies
+     * are typically not directly referenced from a step definition or a driver,
+     * but are used by a deeper component of the application.
+     * <p>
+     * If a stub keeps scenario-specific data (e.g., a stub data store that
+     * keeps in-memory data), the isolation of the scenarios that use the stub
+     * has to be carefully considered. Make sure you check the notes below.
+     * <p>
+     * In our sample, the StubTimeService is a scenario-specific stub, because
+     * it stores a forced local time. The time set by one scenario might confuse
+     * another scenario assuming a different time.
+     * <p>
+     * Notes for ensuring isolation for scenario-specific stubs:
+     * <p>
+     * The Cucumber Spring Boot integration uses a single application context
+     * configuration by default, that creates a single application context
+     * (basically a single app) for executing the scenarios. That means that the
+     * instantiated beans that are used by the application will be reused by
+     * multiple scenario executions, so there is a danger of data leaking. In
+     * order to avoid this the following options can be considered. (This sample
+     * uses option 2.)
+     * <p>
+     * 1. You can enforce having a separate application context for every single
+     * test execution. This can be achieved by annotating the
+     * CucumberSpringConfiguration class with `@DirtiesContext(classMode =
+     * DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)`. Unfortunately this
+     * option causes an extra performance penalty and is currently not supported
+     * by Cucumber with parallel test execution. But for serial execution, when
+     * the application startup time is fast, this can be a feasible option.
+     * <p>
+     * 2. (recommended) The simplest approach is to annotate scenario-specific
+     * stub beans with `@ScenarioScope`. This will inject a proxy bean to the
+     * (shared) application context that stores the data in a thread-local
+     * storage. This means that even though the different scenarios share the
+     * same application context, each scenario will "see" its own version of the
+     * stub, so they can store data to them without influencing the other
+     * scenarios. Unfortunately this works only if the stub is used by the same
+     * thread that initiated the scenario execution. If the dependency is used
+     * by the application from a background thread, for example, this approach
+     * cannot be used. Accessing the stubs will lead to an exception.
+     * <p>
+     * 3. When none of the other options can be used, the stub needs to be
+     * treated as a shared resource and the data stored in it must be managed by
+     * the scenarios. The Reset Before pattern and the other patterns described
+     * in chapter 18 can be used in this case. Note: the patterns in chapter 18
+     * do not support parallel execution automatically. In order to use this
+     * approach for parallel execution, the Resource Pooling pattern can be
+     * applied. As these stubs are bound to the Application Context, the
+     * application context itself has to be pooled (i.e., ensure that one
+     * application context is only used by a single scenario at a time). The
+     * PooledContextCustomizerFactory class implements such a pooled application
+     * context. Check the class documentation for details on how to enable it.
+     */
+    @TestConfiguration
+    public static class StubDependencyConfiguration {
+        /**
+         * Exposing the stub time service as TimeService. We also expose the
+         * StubTimeService class, because some step definitions need to access
+         * stub-specific API (the 'setCurrentTime' function).
+         * <p>
+         * Note that the '@TestBean' annotation cannot be used for replacing
+         * components with scenario-specific stubs, because that supports only
+         * singleton stubs.
+         * <p>
+         * Unlike '@TestBean', the '@Bean' annotation does not "replace" the
+         * original bean, therefore additional configuration is needed to ensure
+         * that the stub is used in scenarios where it is required.
+         * <p>
+         * In this sample we use '@AutoConfiguration' in the application for the
+         * classes that are potential candidates for stubbing with
+         * '@ConditionalOnMissingBean' annotation (see WimpAutoConfiguration
+         * class) which ensures that it will only be used if no other classes
+         * expose the interface. Because the stub exposes it, the real
+         * implementation will not be used.
+         * <p>
+         * If this approach does not work, you can introduce additional
+         * conditions on the real implementation and ensure that those
+         * conditions are not met for testing. The easiest is to exclude it from
+         * testing with `@Profile("!test")`.
+         */
+        @Bean
+        // marked as scenario-scoped because it stores scenario-specific data (the forced local time), see notes above.
+        @ScenarioScope
+        public StubTimeService stubTimeService() {
+            return new StubTimeService();
+        }
+    }
 }
```

### src/test/java/com/wimp/app/specs/drivers/TimeServiceDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/TimeServiceDriver.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/TimeServiceDriver.java#L1-L25)</sub>

```diff
@@ -0,0 +1,25 @@
+package com.wimp.app.specs.drivers;
+
+import com.wimp.app.specs.support.StubTimeService;
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

### src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L2)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/OrderingStepDefinitions.java#L5-L39)</sub>

```diff
@@ -2,42 +2,40 @@ package com.wimp.app.specs.stepdefinitions;
 
 import com.wimp.app.restapi.PlaceOrderRequest;
 import com.wimp.app.specs.drivers.OrderingApiDriver;
+import com.wimp.app.specs.drivers.TimeServiceDriver;
 import com.wimp.app.specs.support.OrderData;
 import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
 import io.cucumber.java.en.*;
 
-import java.time.LocalDateTime;
 import java.time.LocalTime;
 import java.util.List;
 
 public class OrderingStepDefinitions {
     private final OrderingApiDriver orderingApiDriver;
+    private final TimeServiceDriver timeServiceDriver;
 
-    public OrderingStepDefinitions(OrderingApiDriver orderingApiDriver) {
+    public OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, TimeServiceDriver timeServiceDriver) {
         this.orderingApiDriver = orderingApiDriver;
+        this.timeServiceDriver = timeServiceDriver;
     }
 
     @Given("they have placed an order with")
     public void theyHavePlacedAnOrderWith(List<OrderData> orderDataList) throws Exception {
         var orderData = orderDataList.getFirst(); // a single-row data table is expected for this step
-        var expectedDeliveryTime = LocalDateTime.now().with(orderData.getExpectedDeliveryTime());
-        //NOTE: The expectedDeliveryTime is not used, because of the workaround we apply. It will be used once the pattern is applied.
 
-        // With the real time service we cannot fast-forward time, so cannot use the specified
-        // expectedDeliveryTime. Instead, we force the expected delivery time being in 0.5 seconds,
-        // and we wait in the whenTheDeliveryHasNotBeenMadeBy method for the background timer loop
-        // to process the subscription.
+        // ensuring that the placing time is before the expected delivery time
+        timeServiceDriver.setCurrentTime(orderData.getExpectedDeliveryTime().minusMinutes(5));
+
+        // preparing a place order request with expected delivery time (this setting is only available for testing)
         PlaceOrderRequest placeOrderRequest = new PlaceOrderRequestObjectMother()
-            .withExpectedDeliveryTime(LocalDateTime.now().plusNanos(500_000_000))
+            .withExpectedDeliveryTime(timeServiceDriver.getTodayTime(orderData.getExpectedDeliveryTime()))
             .build();
 
         orderingApiDriver.placeOrder(placeOrderRequest).execute();
     }
 
     @When("the delivery has not been made by {time}")
-    public void whenTheDeliveryHasNotBeenMadeBy(LocalTime time) throws InterruptedException {
-        //WORKAROUND: see notes above!
-        //NOTE: The time is not used, because of the workaround we apply. It will be used once the pattern is applied.
-        Thread.sleep(2_000);
+    public void whenTheDeliveryHasNotBeenMadeBy(LocalTime time) {
+        timeServiceDriver.setCurrentTime(time);
     }
 }
```

### src/test/java/com/wimp/app/specs/support/StubTimeService.java

[View file](After/src/test/java/com/wimp/app/specs/support/StubTimeService.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/StubTimeService.java#L1-L52)</sub>

```diff
@@ -0,0 +1,52 @@
+package com.wimp.app.specs.support;
+
+import com.wimp.app.services.TimeService;
+
+import java.time.LocalDateTime;
+import java.util.ArrayList;
+import java.util.List;
+import java.util.function.Predicate;
+
+/**
+ * This is a stub implementation of the TimeService interface for testing
+ * purposes. An alternative approach would be to use the "RealTimeService"
+ * implementation and mock the Clock dependency, but this approach also
+ * simplifies the time-based subscription logic for testing.
+ * <p>
+ * For an idiomatic Spring Boot solution for time-related functionality, check
+ * out the comments in the "TimeService" interface in the main source code.
+ * <p>
+ * The configuration that exposes the StubTimeService and uses it as TimeService
+ * can be found at the CucumberSpringConfiguration.StubDependencyConfiguration
+ * class. Check the additional stubbing notes there.
+ *
+ * @see com.wimp.app.CucumberSpringConfiguration.StubDependencyConfiguration
+ */
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
