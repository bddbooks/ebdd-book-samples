package com.wimp.app.specs.support;

import com.wimp.app.config.AppConfigurationProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.JdbcConnectionDetails;
import org.springframework.stereotype.Component;

/**
 * Exposes test-only configuration settings (bound from
 * {@code application-test.properties} entries with the "test." prefix), plus a
 * couple of settings that are shared with the application itself (e.g. market
 * currency/tax, database connection string) and therefore stay defined on
 * {@link AppConfigurationProvider}.
 */
@Component
public record TestConfigurationProvider(
    PaymentGatewayConfiguration paymentGateway,
    TestDatabaseConfiguration testDatabase,
    AppConfigurationProvider.AppMarketConfiguration market,
    JdbcConnectionDetails database,
    AppConfigurationProvider.AppBackdoorConfiguration backdoor) {

    @ConfigurationProperties(prefix = "test.payment-gateway")
    public record PaymentGatewayConfiguration(String url, String apiKey) {
    }

    @ConfigurationProperties(prefix = "test.database")
    public record TestDatabaseConfiguration(Boolean useStub) {
        public TestDatabaseConfiguration {
            if (useStub == null) useStub = true; // set default
        }
    }
}
