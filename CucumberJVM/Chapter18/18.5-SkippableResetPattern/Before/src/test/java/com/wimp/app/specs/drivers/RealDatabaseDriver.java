package com.wimp.app.specs.drivers;

import org.hibernate.metamodel.MappingMetamodel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(name = "test.database.use-stub", havingValue = "false", matchIfMissing = true)
public class RealDatabaseDriver implements DatabaseDriver {
    private static final Logger log = LoggerFactory.getLogger(RealDatabaseDriver.class);

    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    public RealDatabaseDriver(DataSource dataSource, EntityManagerFactory entityManagerFactory) {
        this.dataSource = dataSource;
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
        List<String> tablesToEmpty = getAllTables();
        emptyTables(tablesToEmpty);
    }

    private void emptyTables(List<String> tables) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0;");
            try {
                for (String table : tables) {
                    statement.execute("TRUNCATE TABLE " + table + ";");
                }
            } finally {
                statement.execute("SET FOREIGN_KEY_CHECKS = 1;");
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to empty database tables", ex);
        }
    }

    private List<String> getAllTables() {
        var metamodel = (MappingMetamodel)entityManagerFactory.getMetamodel();
        List<String> tableNames = new ArrayList<>();

        // Extract physical table names for standard entities
        metamodel.forEachEntityDescriptor(entityDescriptor -> tableNames.add(entityDescriptor.getTableName()));

        // Extract physical table names for @CollectionTable (sub-tables)
        metamodel.forEachCollectionDescriptor(collectionDescriptor -> tableNames.add(collectionDescriptor.getTableName()));

        return tableNames;
    }
}
