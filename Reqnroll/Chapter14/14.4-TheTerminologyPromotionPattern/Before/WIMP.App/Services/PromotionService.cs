using WIMP.App.Infrastructure;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class PromotionService(DataContext dataContext)
{
    public void ActivatePromotion(string promotionName)
    {
        var promotion = new Promotion(promotionName, true);
        dataContext.SavePromotion(promotion);
    }

    public bool IsPromotionActive(string promotionName)
    {
        var promotion = dataContext.GetPromotion(promotionName);
        return promotion?.IsActive ?? false;
    }
}
