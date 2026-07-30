using Reqnroll;
using Reqnroll.BoDi;

using WIMP.App.Data;
using WIMP.App.Data.Db;
using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

[Binding]
public class DiConfiguration
{
    [BeforeScenario(Order = -1)]
    public void SetupDependencies(IObjectContainer scenarioContainer)
    {
        if (Environment.GetEnvironmentVariable("WIMP_STUB_DB") == "true")
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
            string connectionString = "Server=localhost;Port=3306;Database=wimp_test_db;User=root;Password=root;";
            scenarioContainer.RegisterInstanceAs(WimpDbContextOptionsBuilder.CreateDbContextFactory(connectionString));
        }
    }
}
