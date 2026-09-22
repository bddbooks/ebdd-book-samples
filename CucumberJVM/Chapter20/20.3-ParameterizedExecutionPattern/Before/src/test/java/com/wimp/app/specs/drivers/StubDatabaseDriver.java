package com.wimp.app.specs.drivers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
public class StubDatabaseDriver implements DatabaseDriver {

    @Override
    public void emptyDatabase() {
        // nop
    }
}
