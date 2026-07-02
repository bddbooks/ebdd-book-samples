using Reqnroll;

using WIMP.App.Models;
using WIMP.App.Services;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class PromotionStepDefinitions(PromotionService promotionService)
{
    private Promotion? activePromotion;

    [When("the customer chooses {string} promotion")]
    public void WhenTheCustomerChoosesPromotion(string promotionName)
    {
        activePromotion = promotionService.ActivatePromotion(promotionName);
    }

    [Then("the following items should be offered")]
    public void ThenTheFollowingItemsShouldBeOffered(DataTable expectedOfferedItemsTable)
    {
        var offeredItems = activePromotion?.OfferedItems
                ?? throw new InvalidOperationException("No active promotion");

        expectedOfferedItemsTable.CompareToSet(offeredItems);
    }
}
