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

using WIMP.App.Infrastructure;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class PromotionService(DataContext dataContext, MenuService menuService)
{
    public Promotion ActivatePromotion(string promotionName)
    {
        IReadOnlyList<OfferedItem> offeredItems = promotionName switch
        {
            "Friday $1 off" => menuService
                .GetItems()
                .Where(item => item.Price > 8.0m)
                .Select(item => new OfferedItem(item.Name, item.Price - 1.0m, item.Price))
                .ToArray(),
            "Veggie week" => menuService
                .GetItems()
                .Where(item => item.Vegetarian)
                .Select(item => new OfferedItem(item.Name, 6.0m, item.Price))
                .ToArray(),
            _ => []
        };

        var promotion = new Promotion(promotionName, true, offeredItems);
        dataContext.SavePromotion(promotion);
        return promotion;
    }
}
