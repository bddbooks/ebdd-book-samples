package com.wimp.specs.support;

import com.wimp.app.WimpApplication;
import com.wimp.app.services.TimeService;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@CucumberContextConfiguration
@SpringBootTest(classes = WimpApplication.class)
@Import({
    CucumberSpringConfiguration.SpecsConfiguration.class,
    CucumberSpringConfiguration.StubDependencyConfiguration.class})
public class CucumberSpringConfiguration {
    @TestConfiguration
    @ComponentScan("com.wimp.specs")
    static class SpecsConfiguration {
        @Bean
        public MockMvc mockMvc(WebApplicationContext webApplicationContext) {
            return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }
    }

    @TestConfiguration
    static class StubDependencyConfiguration {
        @Bean
        public StubTimeService stubTimeService() {
            return new StubTimeService();
        }

        @Bean
        @Primary
        public TimeService timeService(StubTimeService stubTimeService) {
            return stubTimeService;
        }
    }
}
