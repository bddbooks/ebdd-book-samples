using Reqnroll;

using WIMP.Specs.Support.Logging;

namespace WIMP.Specs.Support;

[Binding]
public class Hooks(AppHostingContext appHostingContext, AppLogContext appLogContext,
    IScenarioContext scenarioContext, IReqnrollOutputHelper outputHelper)
{
    [AfterScenario(Order = 0)]
    public void SaveAppLogOnError()
    {
        if (scenarioContext.ScenarioExecutionStatus == ScenarioExecutionStatus.TestError)
        {
            string outputPath = Path.GetFullPath($"app-log-{Guid.NewGuid():N}.txt");
            appLogContext.SaveToFile(outputPath);
            outputHelper.AddAttachment(outputPath);
        }
    }

    [BeforeScenario]
    public void CreateAppHost()
    {
        appHostingContext.AppHost = new WimpAppHost(appLogContext);
    }

    [AfterScenario]
    public void DisposeAppHost()
    {
        appHostingContext.AppHost.Dispose();
    }
}
