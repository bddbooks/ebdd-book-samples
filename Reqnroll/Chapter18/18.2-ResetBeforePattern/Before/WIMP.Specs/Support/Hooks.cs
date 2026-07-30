using Reqnroll;

using WIMP.App.Data;
using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

[Binding]
public class Hooks(AppHostingContext appHostingContext, IDatabaseDriver databaseDriver, IDataRepository dataRepository)
{
    [BeforeScenario(Order = 0)]
    public async Task CreateDatabase()
    {
        await databaseDriver.CreateDatabase();
    }

    [AfterScenario]
    public async Task DropDatabase()
    {
        await databaseDriver.DropDatabase();
    }

    [BeforeScenario(Order = 1)]
    public void CreateAppHost()
    {
        appHostingContext.AppHost = new WimpAppHost(dataRepository);
    }

    [AfterScenario]
    public void DisposeAppHost()
    {
        appHostingContext.AppHost?.Dispose();
    }
}
