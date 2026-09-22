package com.wimp.app;

import com.wimp.app.specs.support.RestApiContext;
import com.wimp.app.specs.support.StubTimeService;

import io.cucumber.spring.CucumberContextConfiguration;
import io.cucumber.spring.ScenarioScope;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcClientHttpRequestFactory;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.service.registry.ImportHttpServices;

/**
 * Configures Spring Boot for Cucumber
 * <p>
 * Note: the database configuration is done through
 * DatabaseContextCustomizerFactory, check the class documentation for details.
 *
 * @see com.wimp.app.specs.support.DatabaseContextCustomizerFactory
 */
@CucumberContextConfiguration
@SpringBootTest
// We enable 'test' Spring profile (gets configuration values from application-test.properties) and
// allows overriding database configuration, see classes StubDatabaseConfiguration and RealDatabaseConfiguration below.
// We also enable 'backdoor-api' profile, because that is needed by the tests.
@ActiveProfiles({"test", "backdoor-api"})
public class CucumberSpringConfiguration {

    @TestConfiguration
    // Enables autoconfiguration for HTTP Service Clients, see https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-http-service-client
    @ImportHttpServices(group = "specs", basePackages = "com.wimp.app.specs")
    static class SpecsConfiguration {
        @Bean
        public MockMvc mockMvc(WebApplicationContext webApplicationContext) {
            return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }

        /**
         * Enable the HTTP Service Clients to use in-process REST API calls (the
         * same infrastructure that supports RestTestClient as well).
         */
        @Bean
        public RestClientHttpServiceGroupConfigurer specsClientHttpServiceGroupConfigurer(MockMvc mockMvc, RestApiContext restApiContext) {
            return groups -> groups.filterByName("specs")
                .forEachClient((_, clientBuilder) -> {
                    clientBuilder.requestFactory(new MockMvcClientHttpRequestFactory(mockMvc));
                    RestApiContext.configureRestApiCall(restApiContext, clientBuilder);
                });
        }
    }

    /**
     * Configures stub dependencies (see Stub Dependency pattern).
     * <p>
     * Stub dependencies in this context are stub implementations of an
     * application interface that can be used for testing. The stub dependencies
     * are typically not directly referenced from a step definition or a driver,
     * but are used by a deeper component of the application.
     * <p>
     * If a stub keeps scenario-specific data (e.g., a stub data store that
     * keeps in-memory data), the isolation of the scenarios that use the stub
     * has to be carefully considered. Make sure you check the notes below.
     * <p>
     * In our sample, the StubTimeService is a scenario-specific stub, because
     * it stores a forced local time. The time set by one scenario might confuse
     * another scenario assuming a different time.
     * <p>
     * Notes for ensuring isolation for scenario-specific stubs:
     * <p>
     * The Cucumber Spring Boot integration uses a single application context
     * configuration by default, that creates a single application context
     * (basically a single app) for executing the scenarios. That means that the
     * instantiated beans that are used by the application will be reused by
     * multiple scenario executions, so there is a danger of data leaking. In
     * order to avoid this the following options can be considered. (This sample
     * uses option 2.)
     * <p>
     * 1. You can enforce having a separate application context for every single
     * test execution. This can be achieved by annotating the
     * CucumberSpringConfiguration class with `@DirtiesContext(classMode =
     * DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)`. Unfortunately this
     * option causes an extra performance penalty and is currently not supported
     * by Cucumber with parallel test execution. But for serial execution, when
     * the application startup time is fast, this can be a feasible option.
     * <p>
     * 2. (recommended) The simplest approach is to annotate scenario-specific
     * stub beans with `@ScenarioScope`. This will inject a proxy bean to the
     * (shared) application context that stores the data in a thread-local
     * storage. This means that even though the different scenarios share the
     * same application context, each scenario will "see" its own version of the
     * stub, so they can store data to them without influencing the other
     * scenarios. Unfortunately this works only if the stub is used by the same
     * thread that initiated the scenario execution. If the dependency is used
     * by the application from a background thread, for example, this approach
     * cannot be used. Accessing the stubs will lead to an exception.
     * <p>
     * 3. When none of the other options can be used, the stub needs to be
     * treated as a shared resource and the data stored in it must be managed by
     * the scenarios. The Reset Before pattern and the other patterns described
     * in chapter 18 can be used in this case. Note: the patterns in chapter 18
     * do not support parallel execution automatically. In order to use this
     * approach for parallel execution, the Resource Pooling pattern can be
     * applied. As these stubs are bound to the Application Context, the
     * application context itself has to be pooled (i.e., ensure that one
     * application context is only used by a single scenario at a time). The
     * PooledContextCustomizerFactory class implements such a pooled application
     * context. Check the class documentation for details on how to enable it.
     */
    @TestConfiguration
    public static class StubDependencyConfiguration {
        /**
         * Exposing the stub time service as TimeService. We also expose the
         * StubTimeService class, because some step definitions need to access
         * stub-specific API (the 'setCurrentTime' function).
         * <p>
         * Note that the '@TestBean' annotation cannot be used for replacing
         * components with scenario-specific stubs, because that supports only
         * singleton stubs.
         * <p>
         * Unlike '@TestBean', the '@Bean' annotation does not "replace" the
         * original bean, therefore additional configuration is needed to ensure
         * that the stub is used in scenarios where it is required.
         * <p>
         * In this sample we use '@AutoConfiguration' in the application for the
         * classes that are potential candidates for stubbing with
         * '@ConditionalOnMissingBean' annotation (see WimpAutoConfiguration
         * class) which ensures that it will only be used if no other classes
         * expose the interface. Because the stub exposes it, the real
         * implementation will not be used.
         * <p>
         * If this approach does not work, you can introduce additional
         * conditions on the real implementation and ensure that those
         * conditions are not met for testing. The easiest is to exclude it from
         * testing with `@Profile("!test")`.
         */
        @Bean
        // marked as scenario-scoped because it stores scenario-specific data (the forced local time), see notes above.
        @ScenarioScope
        public StubTimeService stubTimeService() {
            return new StubTimeService();
        }
    }
}
