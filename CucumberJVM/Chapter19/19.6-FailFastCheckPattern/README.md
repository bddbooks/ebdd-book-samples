# Pattern Differences: 19.6-FailFastCheckPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

In order to trigger a fail-fast check errors do one of the following steps:

* Change the scenario to load CSV data from `realistic-traffic-empty.csv` instead of `realistic-traffic.csv`.
* Return a failed `ServiceResult` from `TestDataService.PrepareSalesTraffic()` method.
  * Setting `app.simulation.bug` to `true` in `src/main/resources/application.properties` activates this bug without the need to change the `OrderService` class.
  * The HTTP Service Clients infrastructure we use already includes such a fail-fast check.


## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsreportingstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/RestApiContext.java](#srctestjavacomwimpappspecssupportrestapicontextjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L34)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/ReportingStepDefinitions.java#L37-L41)</sub>

```diff
@@ -34,6 +34,11 @@ public class ReportingStepDefinitions {
         // A detailed discussion of the TestFileSystem class can be found in chapter 21: Test File System pattern
         Path path = Path.of(testFileSystem.getFeatureInputFolder(), fileName);
         List<String> lines = Files.readAllLines(path);
+        // Fail-fast check: If the CSV file is empty, we can fail because that
+        // might be a sign of some environmental error.
+        if (lines.size() < 2) {
+            throw new IllegalArgumentException("The CSV file '" + path + "' was empty!");
+        }
         var headers = Arrays.asList(lines.getFirst().split(","));
         List<DailyPizzaSales> sales = new ArrayList<>();
         for (String line : lines.subList(1, lines.size())) {
```

### src/test/java/com/wimp/app/specs/support/RestApiContext.java

[View file](After/src/test/java/com/wimp/app/specs/support/RestApiContext.java#L51)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/RestApiContext.java#L54-L57)</sub>

```diff
@@ -51,6 +51,10 @@ public class RestApiContext {
             log.debug("REST API response: {}, Headers: {}, Content: {}", response.getStatusCode(), response.getHeaders(), peekResponseBodyForLogging(response));
             return response;
         });
+        // Fail-fast check: The HTTP Service Clients infrastructure we use
+        // already checks if the REST API request responds with a non-success
+        // status code, so we don't need to add additional fail-fast checks
+        // here.
     }
 
     private static @NonNull String peekRequestBodyForLogging(byte[] body) throws IOException {
```
