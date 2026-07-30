using Microsoft.Extensions.Logging;

namespace WIMP.Specs.Support.Logging;

/// <summary>
/// An <see cref="ILogger"/> implementation that collect all log entries to <see cref="AppLogContext"/>.
/// This is used for the hosted WIMP application to be able to access the log entries produced
/// by the WIMP app.
/// </summary>
public class AppHostLogger(string categoryName, IExternalScopeProvider scopeProvider, AppLogContext appLogContext) : ILogger
{
    public IDisposable BeginScope<TState>(TState state) where TState : notnull
    {
        return scopeProvider.Push(state);
    }

    public bool IsEnabled(LogLevel logLevel)
    {
        return logLevel != LogLevel.None;
    }

    public void Log<TState>(LogLevel logLevel, EventId eventId, TState state, Exception? exception, Func<TState, Exception?, string> formatter)
    {
        if (!IsEnabled(logLevel))
        {
            return;
        }

        string message = formatter(state, exception);
        if (string.IsNullOrWhiteSpace(message) && exception is null)
        {
            return;
        }

        var scopes = new List<string>();
        scopeProvider.ForEachScope(
            (scope, list) =>
            {
                if (scope is not null)
                {
                    list.Add(scope.ToString()!);
                }
            },
            scopes);

        string scopeSuffix = scopes.Count == 0
            ? string.Empty
            : $" => {string.Join(" => ", scopes)}";

        string logMessage = $"{DateTimeOffset.Now:HH:mm:ss} {logLevel}: {categoryName}[{eventId}]{scopeSuffix}: {message}";
        if (exception is not null)
        {
            logMessage += Environment.NewLine + exception;
        }
        appLogContext.AddLogMessage(logMessage);
    }
}
