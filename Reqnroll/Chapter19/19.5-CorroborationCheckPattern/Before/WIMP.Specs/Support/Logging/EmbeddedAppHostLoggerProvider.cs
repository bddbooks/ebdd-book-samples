using Microsoft.Extensions.Logging;

namespace WIMP.Specs.Support.Logging;

/// <summary>
/// An <see cref="ILoggerProvider"/> implementation that creates <see cref="ILogger"/> loggers,
/// that forwards log entries to Reqnroll output helper via the <see cref="ReqnrollLogger"/> base class.
/// </summary>
public sealed class EmbeddedAppHostLoggerProvider(ReqnrollLoggerProvider reqnrollLoggerProvider) : ILoggerProvider
{
    public ILogger CreateLogger(string categoryName) =>
        reqnrollLoggerProvider.CreateEmbeddedAppHostLogger(categoryName);

    public void Dispose()
    {
    }
}
