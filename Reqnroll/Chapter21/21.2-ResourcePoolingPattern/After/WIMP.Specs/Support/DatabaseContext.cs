using Microsoft.EntityFrameworkCore;

using WIMP.App.Data.Db;
using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

public class DatabaseContext(IDbContextFactory<WimpDbContext> dbContextFactory) : IDisposable
{
    public IDbContextFactory<WimpDbContext> DbContextFactory => dbContextFactory;

    private int isLeased;

    internal bool TryLease() => Interlocked.Exchange(ref isLeased, 1) == 0;

    internal bool TryRelease() => Interlocked.Exchange(ref isLeased, 0) == 1;

    public void Dispose()
    {
        DatabasePoolDriver.ReleaseDatabase(this);
    }
}
