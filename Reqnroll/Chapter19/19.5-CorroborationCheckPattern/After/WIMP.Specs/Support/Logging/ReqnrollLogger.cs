using Microsoft.Extensions.Logging;

using Reqnroll;

namespace WIMP.Specs.Support.Logging;

/// <summary>
/// An <see cref="ILogger"/> implementation that forwards log entries to Reqnroll output helper.
/// </summary>
internal class ReqnrollLogger(
    string categoryName,
    IExternalScopeProvider scopeProvider,
    IReqnrollOutputHelper outputHelper) : ILogger
{
    protected string CategoryName => categoryName;
    protected virtual string DisplayPrefix => "";
    protected virtual LogLevel MinimumLevel => LogLevel.Trace;

    public bool IsEnabled(LogLevel logLevel) => logLevel != LogLevel.None && logLevel >= MinimumLevel;

    public IDisposable BeginScope<TState>(TState state) where TState : notnull =>
        scopeProvider.Push(state);

    public void Log<TState>(
        LogLevel logLevel,
        EventId eventId,
        TState state,
        Exception? exception,
        Func<TState, Exception?, string> formatter)
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

        string indent = new string(' ', scopes.Count * 2 + 2);
        message = message.Replace("\n", "\n" + indent);

        string displayCategoryName = CategoryName.Split('.').Last();
        string displayLogLevel = logLevel == LogLevel.Information ? "Info" : logLevel.ToString();

        outputHelper.WriteLine(
            $"{indent}{DateTimeOffset.Now:HH:mm:ss} {DisplayPrefix}{displayLogLevel}: {displayCategoryName}[{eventId}]{scopeSuffix}: {message}");

        if (exception is not null)
        {
            outputHelper.WriteLine(exception.ToString());
        }
    }
}
