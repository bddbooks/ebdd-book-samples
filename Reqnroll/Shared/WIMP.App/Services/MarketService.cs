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

namespace WIMP.App.Services;

public class MarketService
{
    private readonly string currency;
    private readonly decimal salesTaxPercent;

    public string GetCurrency() => currency;
    public decimal GetSalesTax() => salesTaxPercent;

    private MarketService(string currency, decimal salesTaxPercent)
    {
        this.currency = currency;
        this.salesTaxPercent = salesTaxPercent;
    }

    public MarketService() : this("USD", 6.0m)
    {
    }

    public static MarketService Create(string configuredCurrency, decimal configuredSalesTaxPercent)
    {
        return new MarketService(configuredCurrency, configuredSalesTaxPercent);
    }
}
