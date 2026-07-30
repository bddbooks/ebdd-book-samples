using System.Collections.Concurrent;

using Microsoft.Extensions.Logging;

using Reqnroll;

namespace WIMP.Specs.Support.Logging;

/// <summary>
/// An <see cref="ILoggerProvider"/> implementation that creates <see cref="ILogger"/> loggers,
/// that forward the log messages to Reqnroll output helper.
/// </summary>
public sealed class ReqnrollLoggerProvider(IReqnrollOutputHelper outputHelper) : ILoggerProvider
{
    private readonly IExternalScopeProvider scopeProvider = new ScenarioScopeProvider();

    /// <summary>
    /// The custom <see cref="IExternalScopeProvider"/> implementation is needed for using
    /// scopes as test hierarchy.
    /// </summary>
    private class ScenarioScopeProvider : IExternalScopeProvider
    {
        private readonly ConcurrentStack<object?> scopeStack = new();

        private class StackPopper(ScenarioScopeProvider scopeProvider) : IDisposable
        {
            public void Dispose()
            {
                scopeProvider.scopeStack.TryPop(out _);
            }
        }

        public void ForEachScope<TState>(Action<object?, TState> callback, TState state)
        {
            foreach (object? scope in scopeStack.ToArray())
            {
                callback(scope, state);
            }
        }

        public IDisposable Push(object? state)
        {
            scopeStack.Push(state);
            return new StackPopper(this);
        }
    }

    public ILogger CreateLogger(string categoryName) =>
        new ReqnrollLogger(categoryName, scopeProvider, outputHelper);

    public void Dispose()
    {
    }
}
