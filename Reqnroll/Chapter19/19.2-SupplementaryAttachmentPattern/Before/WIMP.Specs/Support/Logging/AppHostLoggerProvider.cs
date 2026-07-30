using Microsoft.Extensions.Logging;

namespace WIMP.Specs.Support.Logging;

/// <summary>
/// An <see cref="ILoggerProvider"/> implementation that creates <see cref="ILogger"/> loggers,
/// that collect all log entries to <see cref="AppLogContext"/>.
/// This is used for the hosted WIMP application to be able to access the log entries produced
/// by the WIMP app.
/// </summary>
public sealed class AppHostLoggerProvider(AppLogContext appLogContext) : ILoggerProvider, ISupportExternalScope
{
    private IExternalScopeProvider scopeProvider = new LoggerExternalScopeProvider();

    public ILogger CreateLogger(string categoryName) =>
        new AppHostLogger(categoryName, scopeProvider, appLogContext);

    public void SetScopeProvider(IExternalScopeProvider newScopeProvider)
    {
        scopeProvider = newScopeProvider;
    }

    public void Dispose()
    {
    }
}
