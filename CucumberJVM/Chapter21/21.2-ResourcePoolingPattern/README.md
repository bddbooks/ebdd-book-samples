# Pattern Differences: 21.2-ResourcePoolingPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

Note: 
* Using real database for testing is enabled by default for this sample using `configuration`/`systemPropertyVariables` section of the `maven-surefire-plugin` in `pom.xml`.
* The 'After' version of the sample is configured to run parallel (see related settings in `src/test/resources/junit-platform.properties`) and uses database pooling enabled in the `configuration`/`systemPropertyVariables` section of the `maven-surefire-plugin` in `pom.xml`. 

This sample requires a MySQL database to be running using Docker, therefore you need to have Docker installed and running on your machine.


## Summary of Changes

- 📝 Modified [pom.xml](#pomxml)
- ➕ Added [src/test/java/com/wimp/app/specs/drivers/DatabasePoolDriver.java](#srctestjavacomwimpappspecsdriversdatabasepooldriverjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/DatabaseContext.java](#srctestjavacomwimpappspecssupportdatabasecontextjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/DatabaseContextCustomizerFactory.java](#srctestjavacomwimpappspecssupportdatabasecontextcustomizerfactoryjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/Hooks.java](#srctestjavacomwimpappspecssupporthooksjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java](#srctestjavacomwimpappspecssupporttestconfigurationproviderjava)
- 📝 Modified [src/test/resources/application-test.properties](#srctestresourcesapplicationtestproperties)
- 📝 Modified [src/test/resources/junit-platform.properties](#srctestresourcesjunitplatformproperties)

## Detailed Changes

### pom.xml

[View file](After/pom.xml#L158)

<sub>[Jump to change](After/pom.xml#L161-L162)</sub>

```diff
@@ -158,6 +158,8 @@
           <systemPropertyVariables>
             <!-- These settings enable real database usage by default -->
             <test.database.use-stub>false</test.database.use-stub>
+            <!-- Enable database pooling to be used for parallel execution -->
+            <test.database.use-pool>true</test.database.use-pool>
           </systemPropertyVariables>
           <properties>
             <!-- Work around. Surefire does not include enough
```

### src/test/java/com/wimp/app/specs/drivers/DatabasePoolDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/DatabasePoolDriver.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/DatabasePoolDriver.java#L1-L103)</sub>

```diff
@@ -0,0 +1,103 @@
+package com.wimp.app.specs.drivers;
+
+import com.wimp.app.specs.support.DatabaseContext;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+import org.springframework.context.ConfigurableApplicationContext;
+import org.springframework.context.event.ContextClosedEvent;
+import org.testcontainers.mysql.MySQLContainer;
+
+import java.util.NoSuchElementException;
+import java.util.concurrent.BlockingQueue;
+import java.util.concurrent.LinkedBlockingQueue;
+import java.util.concurrent.atomic.AtomicInteger;
+
+/**
+ * This class maintains a pool of databases to allow parallel test execution
+ * against real databases without cross-scenario interference.
+ * <p>
+ * It uses a single MySQL Testcontainers instance and creates multiple databases
+ * on that server. Alternatively every database could have its own container.
+ * <p>
+ * While the database pool allows releasing leased databases, normally this is
+ * not needed, because the application context(s) will only be disposed at the
+ * end of the test run. But if the application context lifetime strategy is
+ * changed (e.g., by using '@DirtiesContext') the databases will be released
+ * even during the test run.
+ * <p>
+ * Because each database has a unique DatabaseContext class instance, that class
+ * can also be used to store any database-related information (e.g., whether it
+ * is dirty and needs to be reset on next use).
+ */
+public class DatabasePoolDriver {
+    private static final Logger log = LoggerFactory.getLogger(DatabasePoolDriver.class);
+    private static final String DATABASE_NAME_TEMPLATE = "wimp_test_db_%s";
+    private static final AtomicInteger lastDbIndex = new AtomicInteger(0);
+    private static final String INITIAL_DATABASE_NAME = String.format(DATABASE_NAME_TEMPLATE, lastDbIndex.get());
+    private static final BlockingQueue<DatabaseContext> DATABASE_URLS = new LinkedBlockingQueue<>();
+    private static final int POOL_SIZE = 4 + 1; // we need one extra, because Cucumber might use one more thread than the max parallel limit
+    private static MySQLContainer mySQLContainer = null;
+
+    private static void ensureInitialized() {
+        if (mySQLContainer != null)
+            return;
+        mySQLContainer = new MySQLContainer("mysql:8.4")
+            .withDatabaseName(INITIAL_DATABASE_NAME)
+            .withUsername("root") // sa/sa could be used, but we need to create triggers
+            .withPassword("root");
+        mySQLContainer.start();
+        // add the first DB to the pool
+        DATABASE_URLS.add(new DatabaseContext(mySQLContainer.getJdbcUrl(), mySQLContainer.getUsername(), mySQLContainer.getPassword()));
+        log.info("Database pool is initialized");
+    }
+
+    /**
+     * Acquires a database from the database pool and subscribes to its
+     * automatic release when the application context closes.
+     * <p>
+     * Normally, this does not happen because the application context(s) will
+     * only be disposed at the end of the test run. But if the application
+     * context lifetime strategy is changed (e.g., by using '@DirtiesContext')
+     * the databases will be released even during the test run.
+     */
+    public static DatabaseContext acquireDatabaseWithReleaseOnContextClosed(ConfigurableApplicationContext applicationContext) {
+        var databaseContext = DatabasePoolDriver.acquireDatabase();
+        applicationContext.addApplicationListener(event -> {
+            if (event instanceof ContextClosedEvent) {
+                // This runs when Spring closes this specific context instance
+                DatabasePoolDriver.releaseDatabase(databaseContext);
+            }
+        });
+        return databaseContext;
+    }
+
+    /**
+     * Acquires a database from the database pool. The database can be released
+     * using the 'releaseDatabase' method.
+     */
+    public static DatabaseContext acquireDatabase() {
+        ensureInitialized();
+        try {
+            var databaseContext = DATABASE_URLS.remove();
+            databaseContext.setLeased(true);
+            log.info("Pooled database leased: {}", databaseContext.url());
+            return databaseContext;
+        } catch (NoSuchElementException e) {
+            if (lastDbIndex.get() + 1 >= POOL_SIZE)
+                throw new RuntimeException("The pool reached the maximum number of databases");
+            var newDbIndex = lastDbIndex.incrementAndGet();
+            var newUrl = mySQLContainer.getJdbcUrl().replace(INITIAL_DATABASE_NAME, String.format(DATABASE_NAME_TEMPLATE, newDbIndex) + "?createDatabaseIfNotExist=true");
+            log.info("New pooled database created and leased: {}", newUrl);
+            return new DatabaseContext(newUrl, mySQLContainer.getUsername(), mySQLContainer.getPassword());
+        }
+    }
+
+    /**
+     * Releases a database back to the pool.
+     */
+    public static void releaseDatabase(DatabaseContext databaseContext) {
+        databaseContext.setLeased(false);
+        DATABASE_URLS.add(databaseContext);
+        log.info("Pooled database released: {}", databaseContext.url());
+    }
+}
```

### src/test/java/com/wimp/app/specs/support/DatabaseContext.java

[View file](After/src/test/java/com/wimp/app/specs/support/DatabaseContext.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/DatabaseContext.java#L1-L43)</sub>

```diff
@@ -0,0 +1,43 @@
+package com.wimp.app.specs.support;
+
+
+/**
+ * Context class that holds information about a pooled database.
+ * <p>
+ * Each pooled database has a unique DatabaseContext class instance, therefore
+ * it can also be used to store any database-related information (e.g., whether
+ * it is dirty and needs to be reset on next use).
+ */
+public final class DatabaseContext {
+    private final String url;
+    private final String username;
+    private final String password;
+    private boolean isLeased;
+
+    public DatabaseContext(String url, String username, String password) {
+        this.url = url;
+        this.username = username;
+        this.password = password;
+        this.isLeased = true;
+    }
+
+    public String url() {
+        return url;
+    }
+
+    public String username() {
+        return username;
+    }
+
+    public String password() {
+        return password;
+    }
+
+    public boolean isLeased() {
+        return isLeased;
+    }
+
+    public void setLeased(boolean leased) {
+        isLeased = leased;
+    }
+}
```

### src/test/java/com/wimp/app/specs/support/DatabaseContextCustomizerFactory.java

[View file](After/src/test/java/com/wimp/app/specs/support/DatabaseContextCustomizerFactory.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/DatabaseContextCustomizerFactory.java#L3)</sub>

```diff
@@ -1,5 +1,6 @@
 package com.wimp.app.specs.support;
 
+import com.wimp.app.specs.drivers.DatabasePoolDriver;
 import jakarta.persistence.EntityManagerFactory;
 import org.hibernate.SessionFactory;
 import org.jspecify.annotations.NonNull;
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/DatabaseContextCustomizerFactory.java#L37-L41)</sub>

```diff
@@ -33,8 +34,11 @@ public class DatabaseContextCustomizerFactory implements ContextCustomizerFactor
     public @Nullable ContextCustomizer createContextCustomizer(@NonNull Class<?> testClass, @NonNull List<ContextConfigurationAttributes> configAttributes) {
 
         var useStub = System.getProperty("test.database.use-stub", "true").equalsIgnoreCase("true");
+        var usePool = System.getProperty("test.database.use-pool", "false").equalsIgnoreCase("true");
         if (useStub) {
             return new StubDatabaseContextCustomizer();
+        } else if (usePool) {
+            return new PooledDatabaseContextCustomizer(Thread.currentThread().threadId());
         } else {
             return new RealDatabaseContextCustomizer();
         }
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/DatabaseContextCustomizerFactory.java#L124-L175)</sub>

```diff
@@ -117,4 +121,56 @@ public class DatabaseContextCustomizerFactory implements ContextCustomizerFactor
             return 987;
         }
     }
+
+    /**
+     * A context customizer that ensures that no application context is reused
+     * by tests running in parallel. This is required for test isolation.
+     * <p>
+     * The created application context will still be reused by multiple tests,
+     * but it is ensured that at any time one application context is only used
+     * by one test. This means that the tests can make any modification in the
+     * application state without worrying about side effects on other tests.
+     * <p>
+     * An alternative option to ensure isolation would be to annotate the
+     * CucumberSpringConfiguration class with: `@DirtiesContext(classMode =
+     * DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)` that ensures a fresh
+     * application context for each test, but that comes with an additional
+     * performance penalty and currently does not work with Cucumber if the
+     * tests run parallel.
+     */
+    static class PooledDatabaseContextCustomizer implements ContextCustomizer {
+        private final long threadId;
+
+        private PooledDatabaseContextCustomizer(long threadId) {
+            this.threadId = threadId;
+        }
+
+        @Override
+        public void customizeContext(@NonNull ConfigurableApplicationContext context, @NonNull MergedContextConfiguration mergedConfig) {
+
+            var databaseContext = DatabasePoolDriver.acquireDatabaseWithReleaseOnContextClosed(context);
+            // exposing database context as bean, so that tests can store database-specific information in it, see documentation on DatabaseContext for details
+            context.getBeanFactory().registerSingleton("databaseContext", databaseContext);
+            // updating database settings from the acquired database
+            var propertySource = new MapPropertySource("Pooled MySQLContainer Test Properties", Map.of(
+                "spring.datasource.url", databaseContext.url(),
+                "spring.datasource.username", databaseContext.username(),
+                "spring.datasource.password", databaseContext.password(),
+                "test.application-context-id", threadId, // make the ID of the application context (that is the thread ID for now) available if needed
+                "spring.test.database.replace", "NONE" // prevent any in memory db from replacing the data source, see @AutoConfigureTestDatabase
+            ));
+            context.getEnvironment().getPropertySources().addFirst(propertySource);
+        }
+
+        // Force Spring to see this context as unique based on the thread ID executing it
+        @Override
+        public boolean equals(Object obj) {
+            return obj != null && this.getClass() == obj.getClass() && this.threadId == ((PooledDatabaseContextCustomizer) obj).threadId;
+        }
+
+        @Override
+        public int hashCode() {
+            return Long.hashCode(threadId);
+        }
+    }
 }
```

### src/test/java/com/wimp/app/specs/support/Hooks.java

[View file](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L24)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/Hooks.java#L27)</sub>

```diff
@@ -24,7 +24,7 @@ public class Hooks {
 
     @Before(order = 0)
     public void resetDatabase() {
-        log.info("Test database: use-stub={}", testConfigurationProvider.testDatabase().useStub());
+        log.info("Test database: use-stub={}, use-pool={}", testConfigurationProvider.testDatabase().useStub(), testConfigurationProvider.testDatabase().usePool());
         databaseDriver.emptyDatabase();
         seedMenuData();
     }
```

### src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java

[View file](After/src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java#L25)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/TestConfigurationProvider.java#L28)</sub>

```diff
@@ -25,7 +25,7 @@ public record TestConfigurationProvider(
     }
 
     @ConfigurationProperties(prefix = "test.database")
-    public record TestDatabaseConfiguration(Boolean useStub) {
+    public record TestDatabaseConfiguration(Boolean useStub, boolean usePool) {
         public TestDatabaseConfiguration {
             if (useStub == null) useStub = true; // set default
         }
```

### src/test/resources/application-test.properties

[View file](After/src/test/resources/application-test.properties#L1)

<sub>[Jump to change](After/src/test/resources/application-test.properties#L3)</sub>

```diff
@@ -1,3 +1,3 @@
 # Test-specific configuration settings.
 
-#NOTE: test.database.use-stub can only be specified via command line or pom.xml
+#NOTE: test.database.use-stub and test.database.use-pool can only be specified via command line or pom.xml
```

### src/test/resources/junit-platform.properties

[View file](After/src/test/resources/junit-platform.properties#L1)

<sub>[Jump to change](After/src/test/resources/junit-platform.properties#L1-L22)</sub>

```diff
@@ -1,3 +1,22 @@
-# NOTE: This sample does not support parallel execution because of the H2
-# in-memory database used as a stub. For details on how parallel execution can
-# be used with databases, check Resource Pooling pattern sample (21.2).
+# The following settings enable parallel execution of the tests.
+# See https://cucumber.io/docs/guides/parallel-execution/ for details.
+#
+# Important:
+# 1. The current application setup does not support parallel execution with stub
+#    database, because the H2 in-memory database we use for stub is shared
+#    across parallel running threads. In case parallel execution is required
+#    with stub database, a solution similar to the Resource Pooling pattern
+#    (implemented by DatabaseContextCustomizerFactory.PooledDatabaseContextCustomizer)
+#    should be used with a pool of H2 databases or a scenario-scoped manual
+#    in-memory repository has to be implemented.
+# 2. For running tests parallel with real database, database pooling must be
+#    enabled by setting test.database.use-pool=true from command line
+#    (-Dtest.database.use-pool=true).
+#
+# See more details in the documentation of the DatabaseContextCustomizerFactory
+# and DatabaseContextCustomizerFactory.PooledDatabaseContextCustomizer classes
+# in Resource Pooling sample.
+
+cucumber.execution.parallel.enabled=true
+cucumber.execution.parallel.config.strategy = fixed
+cucumber.execution.parallel.config.fixed.parallelism = 4
```
