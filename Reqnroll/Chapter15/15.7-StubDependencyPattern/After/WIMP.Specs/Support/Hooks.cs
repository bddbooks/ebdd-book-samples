using Reqnroll;

namespace WIMP.Specs.Support;

[Binding]
public class Hooks(StubTimeService stubTimeService, AppHostingContext appHostingContext)
{
    [BeforeScenario]
    public void CreateAppHost()
    {
        appHostingContext.AppHost = new WimpAppHost(stubTimeService);
    }

    [AfterScenario]
    public void DisposeAppHost()
    {
        appHostingContext.AppHost.Dispose();
    }
}
