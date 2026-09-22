/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

package com.wimp.app;

import com.wimp.app.services.PaymentGateway;
import com.wimp.app.services.RealPaymentGateway;
import com.wimp.app.services.RealTimeService;
import com.wimp.app.services.TimeService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * This class contains the "default" bean configuration of those interfaces that
 * we would like to replace with a stub for testing.
 * <p>
 * This class must be listed in
 * src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 * file.
 */
@AutoConfiguration
public class WimpAutoConfiguration {
    @Bean
    // Avoids creating the bean when a stub is used. See documentation on CucumberSpringConfiguration.StubDependencyConfiguration.timeService() for details.
    @ConditionalOnMissingBean
    public TimeService timeService() {
        return new RealTimeService();
    }

    @Bean
    // Avoids creating the bean when a stub is used.
    @ConditionalOnMissingBean
    public PaymentGateway paymentGateway() {
        return new RealPaymentGateway();
    }
}
