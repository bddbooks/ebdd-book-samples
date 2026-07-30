using Microsoft.EntityFrameworkCore;

using MySqlConnector;

using WIMP.App.Data.Db;

namespace WIMP.Specs.Drivers;

public class DatabaseDriver(IDbContextFactory<WimpDbContext> dbContextFactory) : IDatabaseDriver
{
    public async Task EmptyDatabase()
    {
        var tablesToEmpty = GetAllTables();
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
            databaseInitialized = true;
        }
    }
}
