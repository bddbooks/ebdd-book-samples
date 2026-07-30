using Microsoft.Extensions.Logging;

using Reqnroll;

using WIMP.Specs.Support.Logging;

namespace WIMP.Specs.Support;

[Binding]
public class Hooks(AppHostingContext appHostingContext, AppLogContext appLogContext, ReqnrollLoggerProvider reqnrollLoggerProvider,
    ILoggerFactory loggerFactory, IScenarioContext scenarioContext, IReqnrollOutputHelper outputHelper,
    TestFileSystem testFileSystem)
{
    private ILogger Logger => loggerFactory.CreateLogger(GetType());

    [AfterScenario(Order = 0)]
    public void SaveAppLogOnError()
    {
        if (scenarioContext.ScenarioExecutionStatus == ScenarioExecutionStatus.TestError)
        {
            string outputPath = Path.Combine(testFileSystem.OutputFolder,
                testFileSystem.GetScenarioSpecificFileName(".log"));
            appLogContext.SaveToFile(outputPath);
            Logger.LogInformation("Saved app log to {AppLogFile}", outputPath);
            outputHelper.AddAttachment(outputPath);
        }
    }

    [BeforeScenario]
    public void CreateAppHost()
    {
        appHostingContext.AppHost = new WimpAppHost(appLogContext, reqnrollLoggerProvider);
    }

    [AfterScenario]
    public void DisposeAppHost()
    {
        appHostingContext.AppHost.Dispose();
    }
}
