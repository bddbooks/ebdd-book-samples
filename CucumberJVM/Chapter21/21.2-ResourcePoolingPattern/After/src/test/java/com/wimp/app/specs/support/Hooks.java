package com.wimp.app.specs.support;

import com.wimp.app.specs.drivers.DatabaseDriver;
import com.wimp.app.specs.drivers.MenuBackdoorDriver;
import io.cucumber.java.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class Hooks {
    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    private final DatabaseDriver databaseDriver;
    private final MenuBackdoorDriver menuBackdoorDriver;
    private final TestConfigurationProvider testConfigurationProvider;

    public Hooks(DatabaseDriver databaseDriver, MenuBackdoorDriver menuBackdoorDriver, TestConfigurationProvider testConfigurationProvider) {
        this.databaseDriver = databaseDriver;
        this.menuBackdoorDriver = menuBackdoorDriver;
        this.testConfigurationProvider = testConfigurationProvider;
    }

    @Before(order = 0)
    public void resetDatabase() {
        log.info("Test database: use-stub={}, use-pool={}", testConfigurationProvider.testDatabase().useStub(), testConfigurationProvider.testDatabase().usePool());
        databaseDriver.emptyDatabase();
        seedMenuData();
    }

    private void seedMenuData() {
        log.info("Seeding menu data");
        menuBackdoorDriver.setMenuItems(List.of(
            new MenuItemData("Margherita", new BigDecimal("7.99"), 900, true),
            new MenuItemData("Pepperoni", new BigDecimal("9.99"), 1200, false),
            new MenuItemData("Capricciosa", new BigDecimal("8.99"), 1100, false)));
    }
}
