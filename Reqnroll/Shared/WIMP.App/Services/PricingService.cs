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

using WIMP.App.Models;

namespace WIMP.App.Services;

/// <summary>
/// This class is used to demonstrate price calculation scenarios and
/// not used currently for order price calculation.
/// </summary>
public class PricingService(MarketService marketService)
{
    public OrderItemPrice CalculateItemPrice(CurrencyValue netUnitPrice, int quantity)
    {
        return netUnitPrice.Currency != marketService.GetCurrency()
            ? throw new InvalidOperationException($"With this deployment only {marketService.GetCurrency()} currency calculations can be made")
            : new OrderItemPrice(netUnitPrice * quantity, marketService.GetSalesTax());
    }
}
