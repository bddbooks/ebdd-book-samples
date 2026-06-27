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
