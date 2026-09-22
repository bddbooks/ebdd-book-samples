package com.wimp.app.specs.support;

import com.wimp.app.models.CurrencyValue;
import io.cucumber.java.ParameterType;

import java.math.BigDecimal;

public class CustomParameterTypes {

    private final TestConfigurationProvider testConfigurationProvider;

    public CustomParameterTypes(TestConfigurationProvider testConfigurationProvider) {
        this.testConfigurationProvider = testConfigurationProvider;
    }

    @ParameterType(value = "\\[CUR\\]([0-9.]+)", name = "market-currency")
    public CurrencyValue convertCurrency(String value) {
        return new CurrencyValue(new BigDecimal(value), testConfigurationProvider.market().currency());
    }

    @ParameterType(value = "\\[([A-Z]+)\\]", name = "market-decimal")
    public BigDecimal convertMarketDecimal(String configKey) {
        //noinspection SwitchStatementWithTooFewBranches
        return switch (configKey.toLowerCase()){
            case "tax" -> testConfigurationProvider.market().tax();
            default -> throw new IllegalArgumentException("Unable to get market value for key: %s".formatted(configKey));
        };
    }

    @ParameterType("\\d+(?:\\.\\d{1,2})?")
    public BigDecimal decimal(String value){
        return new BigDecimal(value);
    }
}
