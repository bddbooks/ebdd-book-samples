package com.wimp.app.specs.support;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.event.AfterTestExecutionEvent;

import java.util.List;

/**
 * A context customizer factory that supports different test database
 * strategies: stub (using H2), real (using MqSQLContainer) or pooled (for
 * parallel test execution).
 * <p>
 * NOTE: In this sample only stub database can be used. For examples using real
 * databases, check samples from chapter 18 or later.
 * <p>
 * More information about application context usage for testing can be found at
 * https://www.baeldung.com/spring-integration-test-optimize.
 */
public class DatabaseContextCustomizerFactory implements ContextCustomizerFactory {

    @Override
    public @Nullable ContextCustomizer createContextCustomizer(@NonNull Class<?> testClass, @NonNull List<ContextConfigurationAttributes> configAttributes) {

        return new StubDatabaseContextCustomizer();
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
}
