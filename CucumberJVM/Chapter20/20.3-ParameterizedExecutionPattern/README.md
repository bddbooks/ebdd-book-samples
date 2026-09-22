# Pattern Differences: 20.3-ParameterizedExecutionPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

The sample uses stub database by default. If you wish to try it with real database by setting `test.database.use-stub` to `false` from command line or `pom.xml`.

For enabling this from command line invoke:
* `mvn test "-Dtest.database.use-stub=false"`

For enabling this from `pom.xml`:
* Uncomment the line `<test.database.use-stub>` in `configuration`/`systemPropertyVariables` section of the `maven-surefire-plugin`.

Running the sample with real database requires a MySQL database to be running using Docker, therefore you need to have Docker installed and running on your machine.


## Summary of Changes

- 📝 Modified [pom.xml](#pomxml)
- 📝 Modified [src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java](#srctestjavacomwimpappspecsdriversrealdatabasedriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java](#srctestjavacomwimpappspecsdriversstubdatabasedriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/DatabaseContextCustomizerFactory.java](#srctestjavacomwimpappspecssupportdatabasecontextcustomizerfactoryjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/Hooks.java](#srctestjavacomwimpappspecssupporthooksjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java](#srctestjavacomwimpappspecssupporttestconfigurationproviderjava)
- 📝 Modified [src/test/resources/application-test.properties](#srctestresourcesapplicationtestproperties)

## Detailed Changes

### pom.xml

[View file](After/pom.xml#L155)

<sub>[Jump to change](After/pom.xml#L158-L161)</sub>

```diff
@@ -155,6 +155,10 @@
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-surefire-plugin</artifactId>
         <configuration>
+          <systemPropertyVariables>
+            <!-- Uncomment the following line to use real database for testing -->
+            <!--<test.database.use-stub>false</test.database.use-stub>-->
+          </systemPropertyVariables>
           <properties>
             <!-- Work around. Surefire does not include enough
                  information to disambiguate between different
```

### src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java#L7)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/RealDatabaseDriver.java#L10-L11)</sub>

```diff
@@ -7,6 +7,8 @@ import jakarta.persistence.EntityManagerFactory;
 import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
 import org.springframework.stereotype.Component;
 
+@Component
+@ConditionalOnProperty(name = "test.database.use-stub", havingValue = "false", matchIfMissing = true)
 public class RealDatabaseDriver implements DatabaseDriver {
     private static final Logger log = LoggerFactory.getLogger(RealDatabaseDriver.class);
 
```

### src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java#L4)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/StubDatabaseDriver.java#L7)</sub>

```diff
@@ -4,6 +4,7 @@ import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
 import org.springframework.stereotype.Component;
 
 @Component
+@ConditionalOnProperty(name = "test.database.use-stub", havingValue = "true")
 public class StubDatabaseDriver implements DatabaseDriver {
 
     @Override
```

### src/test/java/com/wimp/app/specs/support/DatabaseContextCustomizerFactory.java

[View file](After/src/test/java/com/wimp/app/specs/support/DatabaseContextCustomizerFactory.java#L32)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/DatabaseContextCustomizerFactory.java#L35-L40)</sub>

```diff
@@ -32,7 +32,12 @@ public class DatabaseContextCustomizerFactory implements ContextCustomizerFactor
     @Override
     public @Nullable ContextCustomizer createContextCustomizer(@NonNull Class<?> testClass, @NonNull List<ContextConfigurationAttributes> configAttributes) {
 
-        return new StubDatabaseContextCustomizer();
+        var useStub = System.getProperty("test.database.use-stub", "true").equalsIgnoreCase("true");
+        if (useStub) {
+            return new StubDatabaseContextCustomizer();
+        } else {
+            return new RealDatabaseContextCustomizer();
+        }
     }
 
     /**
```

### src/test/java/com/wimp/app/specs/support/Hooks.java

[View file](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L13)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L16-L27)</sub>

```diff
@@ -13,15 +13,18 @@ public class Hooks {
     private final PaymentGatewaySimulator paymentGatewaySimulator;
     private final PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy;
     private final DatabaseDriver databaseDriver;
+    private final TestConfigurationProvider testConfigurationProvider;
 
-    public Hooks(PaymentGatewaySimulator paymentGatewaySimulator, PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy, DatabaseDriver databaseDriver) {
+    public Hooks(PaymentGatewaySimulator paymentGatewaySimulator, PaymentGatewayScenarioContextProxy paymentGatewayScenarioContextProxy, DatabaseDriver databaseDriver, TestConfigurationProvider testConfigurationProvider) {
         this.paymentGatewaySimulator = paymentGatewaySimulator;
         this.paymentGatewayScenarioContextProxy = paymentGatewayScenarioContextProxy;
         this.databaseDriver = databaseDriver;
+        this.testConfigurationProvider = testConfigurationProvider;
     }
 
     @Before(order = 0)
     public void resetDatabase() {
+        log.info("Test database: use-stub={}", testConfigurationProvider.testDatabase().useStub());
         databaseDriver.emptyDatabase();
     }
 
```

### src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java

[View file](After/src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java#L15)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java#L18)</sub>

```diff
@@ -15,6 +15,7 @@ import org.springframework.stereotype.Component;
 @Component
 public record TestConfigurationProvider(
     PaymentGatewayConfiguration paymentGateway,
+    TestDatabaseConfiguration testDatabase,
     AppConfigurationProvider.AppMarketConfiguration market,
     JdbcConnectionDetails database,
     AppConfigurationProvider.AppBackdoorConfiguration backdoor) {
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java#L26-L32)</sub>

```diff
@@ -22,4 +23,11 @@ public record TestConfigurationProvider(
     @ConfigurationProperties(prefix = "test.payment-gateway")
     public record PaymentGatewayConfiguration(String url, String apiKey) {
     }
+
+    @ConfigurationProperties(prefix = "test.database")
+    public record TestDatabaseConfiguration(Boolean useStub) {
+        public TestDatabaseConfiguration {
+            if (useStub == null) useStub = true; // set default
+        }
+    }
 }
```

### src/test/resources/application-test.properties

[View file](After/src/test/resources/application-test.properties#L2)

<sub>[Jump to change](After/src/test/resources/application-test.properties#L5-L6)</sub>

```diff
@@ -2,3 +2,5 @@
 
 test.payment-gateway.url=http://localhost/pgwsim
 test.payment-gateway.api-key=39845yjkhsd8ke
+
+#NOTE: test.database.use-stub can only be specified via command line or pom.xml
```
