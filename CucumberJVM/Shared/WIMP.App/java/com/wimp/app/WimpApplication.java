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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.wimp.app.config.AppConfigurationProvider;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WimpApplication {
    static void main(String[] args) {
        SpringApplication.run(WimpApplication.class, args);
    }

    public WimpApplication(AppConfigurationProvider appConfigurationProvider) {
        var log = LoggerFactory.getLogger(WimpApplication.class);

        log.info("Starting WIMP application with the following configuration:");
        log.info("  app.simulation.bug = {}", appConfigurationProvider.simulation().bug());
        log.info("  app.simulation.order-rejection = {}", appConfigurationProvider.simulation().orderRejection());
        log.info("  app.backdoor.disable-menu-integrity-check = {}", appConfigurationProvider.backdoor().disableMenuIntegrityCheck());
        log.info("  app.backdoor.allow-overriding-order-times = {}", appConfigurationProvider.backdoor().allowOverridingOrderTimes());
        log.info("  app.market.currency = {}", appConfigurationProvider.market().currency());
        log.info("  app.market.tax = {}", appConfigurationProvider.market().tax());
        log.info("  spring.database.url = {}", appConfigurationProvider.database().getJdbcUrl());
        log.info("  spring.database.username = {}", appConfigurationProvider.database().getUsername());
    }
}
