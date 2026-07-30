using System.Diagnostics;

using Microsoft.Extensions.Logging;

namespace WIMP.Specs.Support;

public abstract class TestAction<TResult>(ILoggerFactory loggerFactory, string actionName, object? input = null)
{
    protected ILogger Logger => loggerFactory.CreateLogger(GetType());
    public string TestActionName => actionName;
    public object? Input { get; } = input;

    protected abstract Task<TResult> DoExecute();

    public async Task<TResult> Execute()
    {
        Logger.LogInformation("Executing {TestActionName} with {Input}...", TestActionName, Input);
        var stopwatch = Stopwatch.StartNew();
        try
        {
            TResult result;
            using (Logger.BeginScope($"TestAction.{TestActionName}"))
            {
                result = await DoExecute();
            }
            Logger.LogInformation("{TestActionName} executed successfully in {Duration} with { Result}.", TestActionName, stopwatch.Elapsed, result);
            return result;
        }
        catch (Exception ex)
        {
            Logger.LogError("{TestActionName} failed: {Message}", TestActionName, ex.Message);
            throw;
        }
    }

    public async Task<TestActionResult<TResult>> AttemptExecute()
    {
        try
        {
            var result = await Execute();
            return TestActionResult<TResult>.CreateSucceeded(result);
        }
        catch (TestActionFailedException error)
        {
            return TestActionResult<TResult>.CreateFailed(error);
        }
    }
}
