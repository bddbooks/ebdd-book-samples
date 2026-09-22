package com.wimp.app.specs.drivers;

import java.util.Collection;

public interface DatabaseDriver {
    default void emptyDatabase() {
        emptyDatabase(null);
    }

    void emptyDatabase(Collection<String> exceptTables);

    boolean wasTableModified(String tableName);

    void resetTableModificationTracking();
}
