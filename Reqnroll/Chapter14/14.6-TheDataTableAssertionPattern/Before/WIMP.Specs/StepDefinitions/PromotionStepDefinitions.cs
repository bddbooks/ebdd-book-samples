using Reqnroll;

using WIMP.App.Models;
using WIMP.App.Services;
using WIMP.Specs.Support;

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

        var expectedOfferedItems = expectedOfferedItemsTable.CreateSet<OfferedItemData>().ToList();

        int itemsToCompare = Math.Min(offeredItems.Count, expectedOfferedItems.Count);
        for (int i = 0; i < itemsToCompare; i++)
        {
            var expectedItem = expectedOfferedItems[i];
            var actualItem = offeredItems[i];

            Assert.AreEqual(expectedItem.Name, actualItem.Name);
            if (expectedItem.Price != null)
            {
                Assert.AreEqual(expectedItem.Price, actualItem.Price);
            }

            if (expectedItem.OriginalPrice != null)
            {
                Assert.AreEqual(expectedItem.OriginalPrice, actualItem.OriginalPrice);
            }
        }

        Assert.HasCount(
            expectedOfferedItems.Count,
            offeredItems,
            "The offered item count is different from the expected");
    }
}
