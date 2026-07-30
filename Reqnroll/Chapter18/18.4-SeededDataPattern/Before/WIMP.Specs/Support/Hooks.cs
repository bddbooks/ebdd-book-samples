using Reqnroll;

using WIMP.App.Data;
using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

[Binding]
public class Hooks(AppHostingContext appHostingContext, IDatabaseDriver databaseDriver, IDataRepository dataRepository)
{
    [BeforeScenario(Order = 0)]
    public async Task ResetDatabase()
    {
        await databaseDriver.UpgradeSchemaIfNeeded();
        await databaseDriver.EmptyDatabase();
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
