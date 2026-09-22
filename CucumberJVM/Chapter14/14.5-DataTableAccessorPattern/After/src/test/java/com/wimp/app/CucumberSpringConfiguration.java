package com.wimp.app;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@CucumberContextConfiguration
public class CucumberSpringConfiguration {
    @TestConfiguration
    @ComponentScan(basePackages = "com.wimp.app")
    static class SpecsConfiguration {

    }
}
