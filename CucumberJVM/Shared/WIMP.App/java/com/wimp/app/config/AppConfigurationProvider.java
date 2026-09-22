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

package com.wimp.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.JdbcConnectionDetails;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public record AppConfigurationProvider(AppMarketConfiguration market, AppBackdoorConfiguration backdoor, AppSimulationConfiguration simulation, JdbcConnectionDetails database) {

    @ConfigurationProperties(prefix = "app.backdoor")
    public record AppBackdoorConfiguration(boolean disableMenuIntegrityCheck, boolean allowOverridingOrderTimes) {
    }

    @ConfigurationProperties(prefix = "app.market")
    public record AppMarketConfiguration(String currency, BigDecimal tax) {
    }

    @ConfigurationProperties(prefix = "app.simulation")
    public record AppSimulationConfiguration(boolean bug, boolean orderRejection) {
    }
}
