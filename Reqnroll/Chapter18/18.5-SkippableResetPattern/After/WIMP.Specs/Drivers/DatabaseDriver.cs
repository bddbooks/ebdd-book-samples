using Microsoft.EntityFrameworkCore;

using MySqlConnector;

using WIMP.App.Data.Db;

namespace WIMP.Specs.Drivers;

public class DatabaseDriver(IDbContextFactory<WimpDbContext> dbContextFactory) : IDatabaseDriver
{
    public async Task EmptyDatabase(IReadOnlyCollection<string>? exceptTables = null)
    {
        var tablesToEmpty = GetAllTables();
        if (exceptTables != null)
        {
            tablesToEmpty = tablesToEmpty.Where(t => !exceptTables.Contains(t));
        }
        await EmptyTables(tablesToEmpty);
    }

    private async Task EmptyTables(IEnumerable<string> tables)
    {
        await using var db = await dbContextFactory.CreateDbContextAsync();
        await using var connection = (MySqlConnection)db.Database.GetDbConnection();
        await connection.OpenAsync();

        await using (var fkCheckOff = new MySqlCommand("SET FOREIGN_KEY_CHECKS = 0;", connection))
        {
            await fkCheckOff.ExecuteNonQueryAsync();
        }

        try
        {
            foreach (string table in tables)
            {
                await using var cmd = new MySqlCommand($"TRUNCATE TABLE {table};", connection);
                await cmd.ExecuteNonQueryAsync();
            }
        }
        finally
        {
            await using var fkCheckOn = new MySqlCommand("SET FOREIGN_KEY_CHECKS = 1;", connection);
            await fkCheckOn.ExecuteNonQueryAsync();
        }
    }

    private IEnumerable<string> GetAllTables()
    {
        using var db = dbContextFactory.CreateDbContext();
        return db.Model.GetEntityTypes()
            .Select(et => et.GetTableName()!)
            .Where(t => t != "__EFMigrationsHistory");
    }

    private static bool databaseInitialized = false;

    public async Task UpgradeSchemaIfNeeded()
    {
        if (!databaseInitialized)
        {
            await using var db = await dbContextFactory.CreateDbContextAsync();
            await db.Database.MigrateAsync();
            await EnsureModificationTrackingInfrastructure(db);
            databaseInitialized = true;
        }
    }

    private async Task EnsureModificationTrackingInfrastructure(WimpDbContext db)
    {
        await using var connection = (MySqlConnection)db.Database.GetDbConnection();
        await connection.OpenAsync();

        await using (var createTable = new MySqlCommand("""
            CREATE TABLE IF NOT EXISTS MOD_TRACKING
            (
                TABLE_NAME VARCHAR(64) NOT NULL,
                IS_MODIFIED TINYINT(1) NOT NULL DEFAULT 0,
                PRIMARY KEY (TABLE_NAME)
            );
            INSERT INTO MOD_TRACKING (TABLE_NAME, IS_MODIFIED)
            VALUES ('ANY', 1)
            ON DUPLICATE KEY UPDATE IS_MODIFIED = 1;
            """, connection))
        {
            await createTable.ExecuteNonQueryAsync();
        }

        foreach (string tableName in GetAllTables())
        {
            foreach (string eventName in new[] { "INSERT", "UPDATE", "DELETE" })
            {
                string triggerName = $"TRG_{tableName}_TRACK_{eventName}";
                await using var createTrigger = new MySqlCommand($"""
                    DROP TRIGGER IF EXISTS {triggerName};
                    CREATE TRIGGER {triggerName}
                    AFTER {eventName} ON {tableName}
                    FOR EACH ROW
                    INSERT INTO MOD_TRACKING (TABLE_NAME, IS_MODIFIED)
                    VALUES ('{tableName}', 1)
                    ON DUPLICATE KEY UPDATE IS_MODIFIED = 1;
                    """, connection);
                await createTrigger.ExecuteNonQueryAsync();
            }
        }
    }

    public async Task<bool> WasTableModified(string tableName)
    {
        await using var db = await dbContextFactory.CreateDbContextAsync();
        await using var connection = (MySqlConnection)db.Database.GetDbConnection();
        await connection.OpenAsync();

        await using var cmd = new MySqlCommand("""
            SELECT COALESCE((
                SELECT IS_MODIFIED
                FROM MOD_TRACKING
                WHERE TABLE_NAME = @tableName OR TABLE_NAME = 'ANY'
                LIMIT 1
            ), 0);
            """, connection);
        cmd.Parameters.AddWithValue("@tableName", tableName);

        object? result = await cmd.ExecuteScalarAsync();
        return result is not null && Convert.ToBoolean(result);
    }

    public async Task ResetTableModificationTracking()
    {
        await using var db = await dbContextFactory.CreateDbContextAsync();
        await using var connection = (MySqlConnection)db.Database.GetDbConnection();
        await connection.OpenAsync();

        await using var cmd = new MySqlCommand("TRUNCATE TABLE MOD_TRACKING;", connection);
        await cmd.ExecuteNonQueryAsync();
    }
}
