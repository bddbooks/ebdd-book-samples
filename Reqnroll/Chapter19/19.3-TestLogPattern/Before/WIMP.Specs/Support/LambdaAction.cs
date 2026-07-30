namespace WIMP.Specs.Support;

public class LambdaAction<TResult>(
    string testActionName,
    object? input,
    Func<Task<TResult>> action)
    : TestAction<TResult>(testActionName, input)
{

    /// <summary>
    /// Creates a lambda action without input
    /// </summary>
    public LambdaAction(string testActionName, Func<Task<TResult>> action)
        : this(testActionName, null, action)
    {
    }

    /// <summary>
    /// Creates a lambda action with a synchronous action
    /// </summary>
    public LambdaAction(string testActionName, object? input, Func<TResult> action)
        : this(testActionName, input, () => Task.FromResult(action()))
    {
    }

    /// <summary>
    /// Creates a lambda action with a synchronous action, without input
    /// </summary>
    public LambdaAction(string testActionName, Func<TResult> action)
        : this(testActionName, null, () => Task.FromResult(action()))
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
public class LambdaAction(string testActionName, object? input, Func<Task> action) :
    LambdaAction<VoidReturn>(testActionName, input, async () =>
    {
        await action();
        return VoidReturn.Instance;
    })
{
    /// <summary>
    /// Creates a void-return lambda action without input
    /// </summary>
    public LambdaAction(string testActionName, Func<Task> action)
        : this(testActionName, null, action)
    {
    }

    /// <summary>
    /// Creates a void-return lambda action with a synchronous action
    /// </summary>
    public LambdaAction(string testActionName, object? input, Action action)
        : this(testActionName, input, () =>
        {
            action();
            return Task.CompletedTask;
        })
    {
    }

    /// <summary>
    /// Creates a void-return lambda action with a synchronous action, without input
    /// </summary>
    public LambdaAction(string testActionName, Action action)
        : this(testActionName, null, () =>
        {
            action();
            return Task.CompletedTask;
        })
    {
    }
}
