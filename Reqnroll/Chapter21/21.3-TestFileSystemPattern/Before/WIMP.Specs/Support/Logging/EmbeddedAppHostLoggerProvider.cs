using Microsoft.Extensions.Logging;

namespace WIMP.Specs.Support.Logging;

/// <summary>
/// An <see cref="ILoggerProvider"/> implementation that creates <see cref="ILogger"/> loggers,
/// that forwards log entries to Reqnroll output helper via the <see cref="TestLogger"/> base class.
/// </summary>
public sealed class EmbeddedAppHostLoggerProvider(TestLoggerProvider testLoggerProvider) : ILoggerProvider
{
    public ILogger CreateLogger(string categoryName) =>
        testLoggerProvider.CreateEmbeddedAppHostLogger(categoryName);

    public void Dispose()
    {
    }
}
