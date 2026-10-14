using Reqnroll;

using WIMP.Specs.Support.Logging;

namespace WIMP.Specs.Support;

[Binding]
public class Hooks(AppHostingContext appHostingContext, AppLogContext appLogContext, TestLoggerProvider testLoggerProvider)
{
    [BeforeScenario]
    public void CreateAppHost()
    {
        appHostingContext.AppHost = new WimpAppHost(appLogContext, testLoggerProvider);
    }

    [AfterScenario]
    public void DisposeAppHost()
    {
        appHostingContext.AppHost.Dispose();
    }
}
