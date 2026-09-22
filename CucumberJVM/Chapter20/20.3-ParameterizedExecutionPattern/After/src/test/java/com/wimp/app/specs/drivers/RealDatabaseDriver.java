package com.wimp.app.specs.drivers;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "test.database.use-stub", havingValue = "false", matchIfMissing = true)
public class RealDatabaseDriver implements DatabaseDriver {
    private static final Logger log = LoggerFactory.getLogger(RealDatabaseDriver.class);

    private final EntityManagerFactory entityManagerFactory;

    public RealDatabaseDriver(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        upgradeSchemaIfNeeded();
    }

    private void upgradeSchemaIfNeeded() {
        // Hibernate already ensures the database schema is up-to-date at this
        // point: DatabaseConfiguration configures hibernate.hbm2ddl.auto=update
        // on the EntityManagerFactory, which creates/updates tables and columns
        // as needed whenever it is initialized.
    }

    @Override
    public void emptyDatabase() {
        log.info("Emptying database");
        var sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        sessionFactory.getSchemaManager().truncate();
    }
}
