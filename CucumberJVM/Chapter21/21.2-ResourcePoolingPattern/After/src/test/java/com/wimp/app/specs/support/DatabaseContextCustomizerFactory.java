package com.wimp.app.specs.support;

import com.wimp.app.specs.drivers.DatabasePoolDriver;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.event.AfterTestExecutionEvent;
import org.testcontainers.mysql.MySQLContainer;

import java.util.List;
import java.util.Map;

/**
 * A context customizer factory that supports different test database
 * strategies: stub (using H2), real (using MqSQLContainer) or pooled (for
 * parallel test execution).
 * <p>
 * The strategy can be selected by setting the test.database.use-stub and
 * test.database.use-pool properties from command line or from pom.xml.
 * <p>
 * More information about application context usage for testing can be found at
 * https://www.baeldung.com/spring-integration-test-optimize.
 */
public class DatabaseContextCustomizerFactory implements ContextCustomizerFactory {

    @Override
    public @Nullable ContextCustomizer createContextCustomizer(@NonNull Class<?> testClass, @NonNull List<ContextConfigurationAttributes> configAttributes) {

        var useStub = System.getProperty("test.database.use-stub", "true").equalsIgnoreCase("true");
        var usePool = System.getProperty("test.database.use-pool", "false").equalsIgnoreCase("true");
        if (useStub) {
            return new StubDatabaseContextCustomizer();
        } else if (usePool) {
            return new PooledDatabaseContextCustomizer(Thread.currentThread().threadId());
        } else {
            return new RealDatabaseContextCustomizer();
        }
    }

    /**
     * A context customizer that enables stub database (H2). Using the stub
     * database does not require special configuration, but this customizer
     * resets the database after every test to ensure isolation.
     */
    static class StubDatabaseContextCustomizer implements ContextCustomizer {

        @Override
        public void customizeContext(@NonNull ConfigurableApplicationContext context, @NonNull MergedContextConfiguration mergedConfig) {
            context.addApplicationListener(event -> {
                if (event instanceof AfterTestExecutionEvent afterTestExecutionEvent) {
                    var entityManagerFactory = afterTestExecutionEvent.getTestContext().getApplicationContext().getBean(EntityManagerFactory.class);
                    var sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
                    sessionFactory.getSchemaManager().truncate();
                }
            });
        }

        // Each instance of the class must be equal to the other, to avoid creating multiple application contexts
        @Override
        public boolean equals(Object obj) {
            return obj != null && this.getClass() == obj.getClass();
        }

        @Override
        public int hashCode() {
            return 877;
        }
    }

    /**
     * A context customizer that configures a real database using
     * Testcontainers.
     */
    static class RealDatabaseContextCustomizer implements ContextCustomizer {

        /**
         * Sets up a MySQL test container. The database container is started (if
         * needed) in the customizeContext method.
         * <p>
         * If no polymorphic data access behavior is needed (i.e., no need to
         * switch to stub database), you can also use it with auto-config mode
         * using `@Testcontainers` on the class and `@Container` and
         * `@ServiceConnection` in this field. Check
         * https://java.testcontainers.org and
         * https://www.baeldung.com/spring-boot-testcontainers-integration-test
         * for details.
         */
        public static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.4")
            .withDatabaseName("wimp_test_db")
            .withUsername("root") // sa/sa could be used, but we need to create triggers
            .withPassword("root");

        @Override
        public void customizeContext(@NonNull ConfigurableApplicationContext context, @NonNull MergedContextConfiguration mergedConfig) {
            mySQLContainer.start();

            var propertySource = new MapPropertySource("MySQLContainer Test Properties", Map.of(
                "spring.datasource.url", mySQLContainer.getJdbcUrl(),
                "spring.datasource.username", mySQLContainer.getUsername(),
                "spring.datasource.password", mySQLContainer.getPassword(),
                "spring.test.database.replace", "NONE" // prevent any in memory db from replacing the data source, see @AutoConfigureTestDatabase
            ));
            context.getEnvironment().getPropertySources().addFirst(propertySource);
        }

        // Each instance of the class must be equal to the other, to avoid creating multiple application contexts
        @Override
        public boolean equals(Object obj) {
            return obj != null && this.getClass() == obj.getClass();
        }

        @Override
        public int hashCode() {
            return 987;
        }
    }

    /**
     * A context customizer that ensures that no application context is reused
     * by tests running in parallel. This is required for test isolation.
     * <p>
     * The created application context will still be reused by multiple tests,
     * but it is ensured that at any time one application context is only used
     * by one test. This means that the tests can make any modification in the
     * application state without worrying about side effects on other tests.
     * <p>
     * An alternative option to ensure isolation would be to annotate the
     * CucumberSpringConfiguration class with: `@DirtiesContext(classMode =
     * DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)` that ensures a fresh
     * application context for each test, but that comes with an additional
     * performance penalty and currently does not work with Cucumber if the
     * tests run parallel.
     */
    static class PooledDatabaseContextCustomizer implements ContextCustomizer {
        private final long threadId;

        private PooledDatabaseContextCustomizer(long threadId) {
            this.threadId = threadId;
        }

        @Override
        public void customizeContext(@NonNull ConfigurableApplicationContext context, @NonNull MergedContextConfiguration mergedConfig) {

            var databaseContext = DatabasePoolDriver.acquireDatabaseWithReleaseOnContextClosed(context);
            // exposing database context as bean, so that tests can store database-specific information in it, see documentation on DatabaseContext for details
            context.getBeanFactory().registerSingleton("databaseContext", databaseContext);
            // updating database settings from the acquired database
            var propertySource = new MapPropertySource("Pooled MySQLContainer Test Properties", Map.of(
                "spring.datasource.url", databaseContext.url(),
                "spring.datasource.username", databaseContext.username(),
                "spring.datasource.password", databaseContext.password(),
                "test.application-context-id", threadId, // make the ID of the application context (that is the thread ID for now) available if needed
                "spring.test.database.replace", "NONE" // prevent any in memory db from replacing the data source, see @AutoConfigureTestDatabase
            ));
            context.getEnvironment().getPropertySources().addFirst(propertySource);
        }

        // Force Spring to see this context as unique based on the thread ID executing it
        @Override
        public boolean equals(Object obj) {
            return obj != null && this.getClass() == obj.getClass() && this.threadId == ((PooledDatabaseContextCustomizer) obj).threadId;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(threadId);
        }
    }
}
