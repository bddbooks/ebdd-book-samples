using Microsoft.EntityFrameworkCore;

using WIMP.App.Data.Db;

namespace WIMP.Specs.Drivers;

public class DatabaseDriver(IDbContextFactory<WimpDbContext> dbContextFactory) : IDatabaseDriver
{
    public async Task RecreateDatabase()
    {
        await using var db = await dbContextFactory.CreateDbContextAsync();
        await db.Database.EnsureDeletedAsync();
        await db.Database.MigrateAsync(); // this call ensures the creation of the database
    }
}
