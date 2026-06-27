using System.Diagnostics;

namespace WIMP.Specs.Support;

public abstract class TestAction<TResult>(string actionName)
{
    public string TestActionName => actionName;

    protected abstract Task<TResult> DoExecute();

    public async Task<TResult> Execute()
    {
        Console.WriteLine($"Executing {TestActionName}...");
        var stopwatch = Stopwatch.StartNew();
        try
        {
            var result = await DoExecute();
            Console.WriteLine($"{TestActionName} executed successfully in {stopwatch.Elapsed}.");
            return result;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"{TestActionName} failed: {ex.Message}");
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
        catch (WimpActionFailedException error)
        {
            return TestActionResult<TResult>.CreateFailed(error);
        }
    }
}
