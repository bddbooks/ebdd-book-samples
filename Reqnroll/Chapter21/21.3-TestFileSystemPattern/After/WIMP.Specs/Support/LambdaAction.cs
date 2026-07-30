using Microsoft.Extensions.Logging;

namespace WIMP.Specs.Support;

public class LambdaAction<TResult>(
    ILoggerFactory loggerFactory,
    string testActionName,
    object? input,
    Func<Task<TResult>> action)
    : TestAction<TResult>(loggerFactory, testActionName, input)
{

    /// <summary>
    /// Creates a lambda action without input
    /// </summary>
    public LambdaAction(ILoggerFactory loggerFactory, string testActionName, Func<Task<TResult>> action)
        : this(loggerFactory, testActionName, null, action)
    {
    }

    /// <summary>
    /// Creates a lambda action with a synchronous action
    /// </summary>
    public LambdaAction(ILoggerFactory loggerFactory, string testActionName, object? input, Func<TResult> action)
        : this(loggerFactory, testActionName, input, () => Task.FromResult(action()))
    {
    }

    /// <summary>
    /// Creates a lambda action with a synchronous action, without input
    /// </summary>
    public LambdaAction(ILoggerFactory loggerFactory, string testActionName, Func<TResult> action)
        : this(loggerFactory, testActionName, null, () => Task.FromResult(action()))
    {
    }

    protected override async Task<TResult> DoExecute()
    {
        return await action();
    }
}

/// <summary>
/// Lambda action for void-return actions
/// </summary>
public class LambdaAction(ILoggerFactory loggerFactory, string testActionName, object? input, Func<Task> action) :
    LambdaAction<VoidReturn>(loggerFactory, testActionName, input, async () =>
    {
        await action();
        return VoidReturn.Instance;
    })
{
    /// <summary>
    /// Creates a void-return lambda action without input
    /// </summary>
    public LambdaAction(ILoggerFactory loggerFactory, string testActionName, Func<Task> action)
        : this(loggerFactory, testActionName, null, action)
    {
    }

    /// <summary>
    /// Creates a void-return lambda action with a synchronous action
    /// </summary>
    public LambdaAction(ILoggerFactory loggerFactory, string testActionName, object? input, Action action)
        : this(loggerFactory, testActionName, input, () =>
        {
            action();
            return Task.CompletedTask;
        })
    {
    }

    /// <summary>
    /// Creates a void-return lambda action with a synchronous action, without input
    /// </summary>
    public LambdaAction(ILoggerFactory loggerFactory, string testActionName, Action action)
        : this(loggerFactory, testActionName, null, () =>
        {
            action();
            return Task.CompletedTask;
        })
    {
    }
}
