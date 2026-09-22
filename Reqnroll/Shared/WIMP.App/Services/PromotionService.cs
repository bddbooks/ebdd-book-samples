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

using WIMP.App.Data;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class PromotionService(IDataRepository repository)
{
    public Promotion ActivatePromotion(string name)
    {
        var existing = repository.GetPromotionByName(name);

        if (existing is not null)
        {
            return repository.UpdatePromotion(existing, isActive: true);
        }

        var promotion = new Promotion { Name = name, IsActive = true };
        repository.InsertPromotion(promotion);
        return promotion;
    }

    public bool IsPromotionActive(string name) =>
        repository.GetPromotionByName(name)?.IsActive ?? false;
}
