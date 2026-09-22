package com.wimp.app.specs.drivers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@ConditionalOnProperty(name = "test.database.use-stub", havingValue = "true")
public class StubDatabaseDriver implements DatabaseDriver {

    @Override
    public void emptyDatabase(Collection<String> exceptTables) {
        // nop
    }

    @Override
    public boolean wasTableModified(String tableName) {
        return true; // treat it modified, seeding is anyway "free"
    }

    @Override
    public void resetTableModificationTracking() {
        // nop
    }
}
