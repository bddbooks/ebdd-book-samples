using Reqnroll;

namespace WIMP.Specs.Support;

[Binding]
public class Hooks(AppHostingContext appHostingContext)
{
    [BeforeScenario]
    public void CreateAppHost()
    {
        appHostingContext.AppHost = new WimpAppHost();
    }

    [AfterScenario]
    public void DisposeAppHost()
    {
        appHostingContext.AppHost.Dispose();
    }
}
