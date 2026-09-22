package com.wimp.app.specs.drivers;

import com.wimp.app.specs.support.DatabaseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.testcontainers.mysql.MySQLContainer;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class maintains a pool of databases to allow parallel test execution
 * against real databases without cross-scenario interference.
 * <p>
 * It uses a single MySQL Testcontainers instance and creates multiple databases
 * on that server. Alternatively every database could have its own container.
 * <p>
 * While the database pool allows releasing leased databases, normally this is
 * not needed, because the application context(s) will only be disposed at the
 * end of the test run. But if the application context lifetime strategy is
 * changed (e.g., by using '@DirtiesContext') the databases will be released
 * even during the test run.
 * <p>
 * Because each database has a unique DatabaseContext class instance, that class
 * can also be used to store any database-related information (e.g., whether it
 * is dirty and needs to be reset on next use).
 */
public class DatabasePoolDriver {
    private static final Logger log = LoggerFactory.getLogger(DatabasePoolDriver.class);
    private static final String DATABASE_NAME_TEMPLATE = "wimp_test_db_%s";
    private static final AtomicInteger lastDbIndex = new AtomicInteger(0);
    private static final String INITIAL_DATABASE_NAME = String.format(DATABASE_NAME_TEMPLATE, lastDbIndex.get());
    private static final BlockingQueue<DatabaseContext> DATABASE_URLS = new LinkedBlockingQueue<>();
    private static final int POOL_SIZE = 4 + 1; // we need one extra, because Cucumber might use one more thread than the max parallel limit
    private static MySQLContainer mySQLContainer = null;

    private static void ensureInitialized() {
        if (mySQLContainer != null)
            return;
        mySQLContainer = new MySQLContainer("mysql:8.4")
            .withDatabaseName(INITIAL_DATABASE_NAME)
            .withUsername("root") // sa/sa could be used, but we need to create triggers
            .withPassword("root");
        mySQLContainer.start();
        // add the first DB to the pool
        DATABASE_URLS.add(new DatabaseContext(mySQLContainer.getJdbcUrl(), mySQLContainer.getUsername(), mySQLContainer.getPassword()));
        log.info("Database pool is initialized");
    }

    /**
     * Acquires a database from the database pool and subscribes to its
     * automatic release when the application context closes.
     * <p>
     * Normally, this does not happen because the application context(s) will
     * only be disposed at the end of the test run. But if the application
     * context lifetime strategy is changed (e.g., by using '@DirtiesContext')
     * the databases will be released even during the test run.
     */
    public static DatabaseContext acquireDatabaseWithReleaseOnContextClosed(ConfigurableApplicationContext applicationContext) {
        var databaseContext = DatabasePoolDriver.acquireDatabase();
        applicationContext.addApplicationListener(event -> {
            if (event instanceof ContextClosedEvent) {
                // This runs when Spring closes this specific context instance
                DatabasePoolDriver.releaseDatabase(databaseContext);
            }
        });
        return databaseContext;
    }

    /**
     * Acquires a database from the database pool. The database can be released
     * using the 'releaseDatabase' method.
     */
    public static DatabaseContext acquireDatabase() {
        ensureInitialized();
        try {
            var databaseContext = DATABASE_URLS.remove();
            databaseContext.setLeased(true);
            log.info("Pooled database leased: {}", databaseContext.url());
            return databaseContext;
        } catch (NoSuchElementException e) {
            if (lastDbIndex.get() + 1 >= POOL_SIZE)
                throw new RuntimeException("The pool reached the maximum number of databases");
            var newDbIndex = lastDbIndex.incrementAndGet();
            var newUrl = mySQLContainer.getJdbcUrl().replace(INITIAL_DATABASE_NAME, String.format(DATABASE_NAME_TEMPLATE, newDbIndex) + "?createDatabaseIfNotExist=true");
            log.info("New pooled database created and leased: {}", newUrl);
            return new DatabaseContext(newUrl, mySQLContainer.getUsername(), mySQLContainer.getPassword());
        }
    }

    /**
     * Releases a database back to the pool.
     */
    public static void releaseDatabase(DatabaseContext databaseContext) {
        databaseContext.setLeased(false);
        DATABASE_URLS.add(databaseContext);
        log.info("Pooled database released: {}", databaseContext.url());
    }
}
