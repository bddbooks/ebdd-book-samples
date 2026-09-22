package com.wimp.app.specs.support;

import com.wimp.app.specs.drivers.DatabaseDriver;
import io.cucumber.java.*;

public class Hooks {
    private final DatabaseDriver databaseDriver;

    public Hooks(DatabaseDriver databaseDriver) {
        this.databaseDriver = databaseDriver;
    }

    @Before(order = 0)
    public void resetDatabase() {
        databaseDriver.emptyDatabase();
    }
}
