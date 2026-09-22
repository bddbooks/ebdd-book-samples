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

#if DEBUG
/*
 * This class is to support Entity Framework tooling, only needed design-time
 *
 * To add a new migration step after a database schema change invoke:
 *
 * - dotnet ef migrations add "<change-name>" -n "Data.Db.Migrations"
 */

using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;

namespace WIMP.App.Data.Db;

public class WimpDesignTimeDbContextFactory : IDesignTimeDbContextFactory<WimpDbContext>
{
    public WimpDbContext CreateDbContext(string[] args)
    {
        var options = new DbContextOptionsBuilder<WimpDbContext>()
            .UseMySql(WimpDbContextOptionsBuilder.DefaultConnectionString, new MySqlServerVersion(new Version(8, 4, 0)))
            .Options;

        return new WimpDbContext(options);
    }
}
#endif
