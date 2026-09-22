/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;

namespace WIMP.App.Data.Db;

public static class WimpDbContextOptionsBuilder
{
    internal const string DefaultConnectionString =
        "Server=localhost;Port=3306;Database=wimp_db;User=root;Password=root;";

    public static void ConfigureDbContextOptions(this DbContextOptionsBuilder options, string? connectionString = null)
    {
        connectionString ??=
            Environment.GetEnvironmentVariable("WIMP_DB_CONNECTION_STRING")
            ?? DefaultConnectionString;
        options.UseMySql(connectionString, ServerVersion.AutoDetect(connectionString));
    }

    /// <summary>
    /// Creates a <see cref="IDbContextFactory&lt;WimpDbContext&gt;" /> instance that can be used for testing or
    /// for other needs where the factory created by ASP.NET is not available.
    /// </summary>
    public static IDbContextFactory<WimpDbContext> CreateDbContextFactory(string? connectionString = null)
    {
        return new WimpDbContextFactoryImplementation(() =>
        {
            var optionsBuilder = new DbContextOptionsBuilder<WimpDbContext>();
            optionsBuilder.ConfigureDbContextOptions(connectionString);
            return optionsBuilder.Options;
        });
    }

    private class WimpDbContextFactoryImplementation(Func<DbContextOptions<WimpDbContext>> getOptions) : IDbContextFactory<WimpDbContext>
    {
        public WimpDbContext CreateDbContext()
        {
            return new WimpDbContext(getOptions());
        }

        public override string ToString()
        {
            string? connectionString = getOptions().Extensions
                .OfType<RelationalOptionsExtension>().FirstOrDefault()?.ConnectionString;
            return $"WIMP DB {connectionString}";
        }
    }
}
