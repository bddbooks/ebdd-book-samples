using Microsoft.Extensions.Logging;

using Reqnroll;

namespace WIMP.Specs.Support.Logging;

/// <summary>
/// An <see cref="ILogger"/> implementation that forwards log entries to Reqnroll output helper
/// via the <see cref="ReqnrollLogger"/> base class.
/// </summary>
internal class EmbeddedAppHostLogger(string categoryName, IExternalScopeProvider scopeProvider, IReqnrollOutputHelper outputHelper, IScenarioContext scenarioContext) :
    ReqnrollLogger(categoryName, scopeProvider, outputHelper)
{
    private const LogLevel MinimumAppLogLevel = LogLevel.Warning;
    private const LogLevel MinimumAppLogLevelForActions = LogLevel.Information;

    /// <summary>
    /// Gets whether the logger is for a WIMP application log or for ASP.NET infrastructure log
    /// </summary>
    private bool IsWimpCategory => CategoryName.StartsWith("WIMP");

    protected override LogLevel MinimumLevel =>
        scenarioContext.CurrentScenarioBlock == ScenarioBlock.When || IsWimpCategory
            ? MinimumAppLogLevelForActions
            : MinimumAppLogLevel;

    protected override string DisplayPrefix => IsWimpCategory ? "WIMP/" : "ASP.NET/";
}
