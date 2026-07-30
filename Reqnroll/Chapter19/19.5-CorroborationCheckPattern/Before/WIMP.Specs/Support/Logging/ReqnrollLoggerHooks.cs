using Microsoft.Extensions.Logging;

using Reqnroll;

namespace WIMP.Specs.Support.Logging;

[Binding]
public class ReqnrollLoggerHooks(IScenarioContext scenarioContext, ReqnrollLoggerProvider reqnrollLoggerProvider)
{
    private const LogLevel DefaultMinimumLogLevel = LogLevel.Information;

    [BeforeScenario(Order = -1)]
    public void SetupLoggerFactory()
    {
        const string tagPrefix = "log:";
        string? logLevelFromTag = scenarioContext.ScenarioInfo.CombinedTags
            .FirstOrDefault(t => t.StartsWith(tagPrefix))?
            .Substring(tagPrefix.Length);
        var logLevel = logLevelFromTag != null ? Enum.Parse<LogLevel>(logLevelFromTag, true) : DefaultMinimumLogLevel;

        var loggerFactory = LoggerFactory.Create(builder =>
        {
            builder.SetMinimumLevel(logLevel);
            builder.AddProvider(reqnrollLoggerProvider);
        });
        scenarioContext.ScenarioContainer.RegisterInstanceAs(loggerFactory, dispose: true);
    }
}
