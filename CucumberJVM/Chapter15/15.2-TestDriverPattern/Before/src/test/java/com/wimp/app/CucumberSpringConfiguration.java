package com.wimp.app;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
    }
}
