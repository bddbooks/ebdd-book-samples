/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

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
