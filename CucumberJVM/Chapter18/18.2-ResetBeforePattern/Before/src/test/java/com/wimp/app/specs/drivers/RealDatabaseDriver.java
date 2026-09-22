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
    }

    @Override
    public void createDatabase() {
        log.info("Creating database");
        var sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        sessionFactory.getSchemaManager().create(true);
    }

    @Override
    public void dropDatabase() {
        log.info("dropping database");
        var sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        sessionFactory.getSchemaManager().drop(true);
    }
}
