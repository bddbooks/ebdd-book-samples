namespace WIMP.Specs.Support;

public record TestActionResult<TResult>
{
    public static readonly TestActionResult<TResult> NotExecuted =
        new(false, null, default!);

    public static TestActionResult<TResult> CreateSucceeded(
        TResult result = default!) => new(true, null, result);

    public static TestActionResult<TResult> CreateFailed(
        WimpActionFailedException error) => new(false, error, default!);

    public bool WasExecuted => !Equals(NotExecuted);

    public bool Success { get; }

    public WimpActionFailedException? Error { get; }

    public TResult Result { get; }

    private TestActionResult(bool success, WimpActionFailedException? error, TResult result)
    {
        Success = success;
        Error = error;
        Result = result;
    }

    public void AssertExecuted()
    {
        Assert.IsTrue(WasExecuted, "The action was not executed");
    }

    public void AssertFailed()
    {
        AssertExecuted();
        Assert.IsFalse(Success, $"The result ({this}) expected to be failure");
        Assert.IsNotNull(Error, $"The result ({this}) expected to contain an error");
    }

    public void AssertFailedWithErrorMessageContains(string expectedErrorMessage)
    {
        AssertFailed();
        StringAssert.Contains(Error!.Message, expectedErrorMessage);
    }

    public void AssertSucceeded()
    {
        AssertExecuted();
        Assert.IsTrue(Success, $"The result ({this}) expected to succeed");
    }
}
