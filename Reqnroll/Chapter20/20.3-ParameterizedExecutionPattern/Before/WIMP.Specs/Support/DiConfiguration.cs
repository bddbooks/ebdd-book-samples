using Reqnroll;
using Reqnroll.BoDi;

using WIMP.App.Data;
using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

[Binding]
public class DiConfiguration
{
    [BeforeScenario(Order = -1)]
    public void SetupDependencies(IObjectContainer scenarioContainer)
    {
        Console.WriteLine("Using stub database");
        scenarioContainer.RegisterTypeAs<StubDatabaseDriver, IDatabaseDriver>();
        scenarioContainer.RegisterTypeAs<StubDataRepository, IDataRepository>();
    }
}
