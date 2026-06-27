namespace WIMP.App.Models;

public readonly struct ServiceResult<TResult>
{
    public bool Successful { get; }
    public string ErrorMessage { get; }

    public TResult Value => Successful ? field! : default!;

    private ServiceResult(bool successful, TResult? value, string errorMessage)
    {
        Successful = successful;
        Value = value;
        ErrorMessage = errorMessage;
    }

    public static ServiceResult<TResult> Success(TResult result) =>
        new(true, result, string.Empty);
    public static ServiceResult<TResult> Failure(string errorMessage) =>
        new(false, default, errorMessage);
}
