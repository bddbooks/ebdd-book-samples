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

public record CurrencyValue(decimal Value, string Currency)
{
    public override string ToString()
    {
        return $"{Currency} {Value:F2}";
    }

    public static CurrencyValue operator *(CurrencyValue price, int quantity)
    {
        return price with { Value = price.Value * quantity };
    }
    public static CurrencyValue operator *(CurrencyValue price, decimal multiplier)
    {
        return price with { Value = price.Value * multiplier };
    }
    public static CurrencyValue operator +(CurrencyValue a, CurrencyValue b)
    {
        return a.Currency != b.Currency
            ? throw new InvalidOperationException("Currencies must be the same for addition")
            : a with { Value = a.Value + b.Value };
    }
}
