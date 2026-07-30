using Microsoft.EntityFrameworkCore;

using Reqnroll;
using Reqnroll.BoDi;

using WIMP.App.Data;
using WIMP.App.Data.Db;
using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

[Binding]
public class DiConfiguration(TestConfigurationProvider testConfigurationProvider)
{
    [BeforeScenario(Order = -1)]
    public void SetupDependencies(IObjectContainer scenarioContainer)
    {
        if (testConfigurationProvider.Database.UseStub)
        {
            Console.WriteLine("Using stub database");
            scenarioContainer.RegisterTypeAs<StubDatabaseDriver, IDatabaseDriver>();
            scenarioContainer.RegisterTypeAs<StubDataRepository, IDataRepository>();
        }
        else
        {
            Console.WriteLine("Using real database");
            scenarioContainer.RegisterTypeAs<DatabaseDriver, IDatabaseDriver>();
            scenarioContainer.RegisterTypeAs<DataRepository, IDataRepository>();
            // get database context factory from pool
            scenarioContainer.RegisterFactoryAs(DatabasePoolDriver.AcquireDatabase);
            scenarioContainer.RegisterFactoryAs<IDbContextFactory<WimpDbContext>>(di => di.Resolve<DatabaseContext>().DbContextFactory);
        }
    }
}
