using Reqnroll;
using Reqnroll.BoDi;

using WIMP.App.Data;
using WIMP.App.Data.Db;
using WIMP.App.Services;
using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

[Binding]
public class DiConfiguration(TestConfigurationProvider testConfigurationProvider)
{
    [BeforeScenario(Order = -1)]
    public void SetupDependencies(IObjectContainer scenarioContainer)
    {
        scenarioContainer.RegisterInstanceAs(
            MarketService.Create(
                testConfigurationProvider.Market.Currency,
                testConfigurationProvider.Market.Tax));

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
            string connectionString = testConfigurationProvider.Database.ConnectionString;
            scenarioContainer.RegisterInstanceAs(WimpDbContextOptionsBuilder.CreateDbContextFactory(connectionString));
        }
    }
}
