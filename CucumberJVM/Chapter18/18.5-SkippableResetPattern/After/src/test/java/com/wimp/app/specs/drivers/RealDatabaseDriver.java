package com.wimp.app.specs.drivers;

import org.hibernate.metamodel.MappingMetamodel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
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

        // Setting up our own modification-tracking infrastructure.
        ensureModificationTrackingInfrastructure();
    }

    @Override
    public void emptyDatabase(Collection<String> exceptTables) {
        log.info("Emptying database, except {}", exceptTables);
        List<String> tablesToEmpty = getAllTables().stream()
            .filter(table -> exceptTables == null || !exceptTables.contains(table))
            .toList();
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

    private void ensureModificationTrackingInfrastructure() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS MOD_TRACKING
                (
                    TABLE_NAME VARCHAR(64) NOT NULL,
                    IS_MODIFIED TINYINT(1) NOT NULL DEFAULT 0,
                    PRIMARY KEY (TABLE_NAME)
                );
                """);
            statement.execute("""
                INSERT INTO MOD_TRACKING (TABLE_NAME, IS_MODIFIED)
                VALUES ('ANY', 1)
                ON DUPLICATE KEY UPDATE IS_MODIFIED = 1;
                """);

            for (String tableName : getAllTables()) {
                for (String eventName : new String[] {"INSERT", "UPDATE", "DELETE"}) {
                    String triggerName = "TRG_" + tableName + "_TRACK_" + eventName;
                    statement.execute("DROP TRIGGER IF EXISTS " + triggerName + ";");
                    statement.execute("""
                        CREATE TRIGGER %s
                        AFTER %s ON %s
                        FOR EACH ROW
                        INSERT INTO MOD_TRACKING (TABLE_NAME, IS_MODIFIED)
                        VALUES ('%s', 1)
                        ON DUPLICATE KEY UPDATE IS_MODIFIED = 1;
                        """.formatted(triggerName, eventName, tableName, tableName));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to set up modification tracking infrastructure", ex);
        }
    }

    @Override
    public boolean wasTableModified(String tableName) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT COALESCE((
                     SELECT IS_MODIFIED
                     FROM MOD_TRACKING
                     WHERE TABLE_NAME = ? OR TABLE_NAME = 'ANY'
                     LIMIT 1
                 ), 0);
                 """)) {
            statement.setString(1, tableName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getBoolean(1);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to read table modification tracking for " + tableName, ex);
        }
    }

    @Override
    public void resetTableModificationTracking() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE MOD_TRACKING;");
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to reset table modification tracking", ex);
        }
    }
}
