using System.Collections.Concurrent;

using Microsoft.Extensions.ObjectPool;

using WIMP.App.Data.Db;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public static class DatabasePoolDriver
{
    private static readonly string[] connectionStrings = GetConnectionStrings();

    private static readonly TimeSpan acquireTimeout = TimeSpan.FromSeconds(10);

    private static readonly ObjectPool<DatabaseContext> databasePool =
        new DefaultObjectPoolProvider
        {
            MaximumRetained = connectionStrings.Length
        }.Create(new DatabaseContextPooledObjectPolicy(connectionStrings));

    private static readonly SemaphoreSlim availableDatabaseSignal =
        new(initialCount: connectionStrings.Length, maxCount: connectionStrings.Length);

    private static string[] GetConnectionStrings()
    {
        var testConfigurationProvider = new TestConfigurationProvider();
        return Enumerable.Range(0, testConfigurationProvider.Database.PoolSize).Select(i =>
                testConfigurationProvider.Database.PooledConnectionStringTemplate.Replace("{index}", i.ToString()))
            .ToArray();
    }

    public static DatabaseContext AcquireDatabase()
    {
        if (!availableDatabaseSignal.Wait(acquireTimeout))
        {
            throw new TimeoutException(
                $"Timed out after {acquireTimeout.TotalSeconds:0} seconds while waiting for a pooled database context.");
        }

        try
        {
            var databaseContext = databasePool.Get();
            Console.WriteLine($"Using DB from pool: {databaseContext.DbContextFactory}");
            return databaseContext.TryLease() ? databaseContext :
                throw new InvalidOperationException("Internal error: acquired a database context that is already leased.");
        }
        catch
        {
            availableDatabaseSignal.Release();
            throw;
        }
    }

    public static void ReleaseDatabase(DatabaseContext databaseContext)
    {
        ArgumentNullException.ThrowIfNull(databaseContext);

        if (!databaseContext.TryRelease())
        {
            throw new InvalidOperationException("Attempted to release a database context that is not currently leased.");
        }

        databasePool.Return(databaseContext);
        availableDatabaseSignal.Release();
    }

    private sealed class DatabaseContextPooledObjectPolicy(string[] connectionStrings)
        : PooledObjectPolicy<DatabaseContext>
    {
        private readonly ConcurrentQueue<string> remainingConnectionStrings = new(connectionStrings);

        public override DatabaseContext Create()
        {
            if (!remainingConnectionStrings.TryDequeue(out string? connectionString))
            {
                throw new InvalidOperationException(
                    "Internal error: attempted to create more pooled database contexts than configured.");
            }

            Console.WriteLine($"Initializing pooled DB for {connectionString}");
            var dbContextFactory = WimpDbContextOptionsBuilder.CreateDbContextFactory(connectionString);
            var databaseContext = new DatabaseContext(dbContextFactory);
            DatabaseDriver.UpgradeSchemaIfNeeded(databaseContext.DbContextFactory);
            return databaseContext;
        }

        public override bool Return(DatabaseContext obj) => true;
    }
}
