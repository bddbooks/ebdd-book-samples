namespace WIMP.Specs.Support;

public class LambdaAction<TResult>(string testActionName, Func<Task<TResult>> action)
    : TestAction<TResult>(testActionName)
{
    /// <summary>
    /// Creates a lambda action with a synchronous action
    /// </summary>
    public LambdaAction(string testActionName, Func<TResult> action)
        : this(testActionName, () => Task.FromResult(action()))
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
public class LambdaAction(string testActionName, Func<Task> action) :
    LambdaAction<VoidReturn>(testActionName,
        () => action().ContinueWith(_ => VoidReturn.Instance))
{
    /// <summary>
    /// Creates a void-return lambda action with a synchronous action
    /// </summary>
    public LambdaAction(string testActionName, Action action)
        : this(testActionName, () =>
        {
            action();
            return Task.CompletedTask;
        })
    {
    }
}
