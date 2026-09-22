package com.wimp.app.specs.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wimp.app.specs.drivers.DatabaseDriver;
import io.cucumber.java.*;

public class Hooks {
    protected static final Logger log = LoggerFactory.getLogger(Hooks.class);

    private final DatabaseDriver databaseDriver;

    public Hooks(DatabaseDriver databaseDriver) {
        this.databaseDriver = databaseDriver;
    }

    @Before(order = 0)
    public void resetDatabase() {
        databaseDriver.emptyDatabase();
    }
}
