using Reqnroll;

using WIMP.App.Models;
using WIMP.App.RestApi;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class PromotionsStepDefinitions(MenuApiDriver menuApiDriver, PromotionsApiDriver promotionsApiDriver)
{
    private IReadOnlyCollection<OfferedMenuItem>? offeredMenuItems;

    [When("the customer chooses {string} promotion")]
    public async Task WhenTheCustomerChoosesPromotion(string promotionName)
    {
        offeredMenuItems = await menuApiDriver.LoadMenu(promotionName).Execute();
    }

    [Then("the following items should be offered")]
    public void ThenTheFollowingItemsShouldBeOffered(DataTable dataTable)
    {
        var promotionalItems = offeredMenuItems!.Where(mi => mi.OriginalPrice != mi.Price);
        dataTable.CompareToSet(promotionalItems);
    }

    [Given("the {string} promotion is active")]
    public async Task GivenThePromotionIsActive(string promotionName)
    {
        await promotionsApiDriver.ActivatePromotion(new ActivatePromotionRequest(promotionName)).Execute();
    }

    [Then("the customer should receive a {string} coupon via email")]
    public async Task ThenTheCustomerShouldReceiveACouponViaEmail(string couponCode)
    {
        var coupons = await promotionsApiDriver.GetCoupons(DomainDefaults.CustomerEmail).Execute();
        Assert.IsTrue(coupons.Any(c => string.Equals(c.Code, couponCode, StringComparison.OrdinalIgnoreCase)),
            $"Expected coupon '{couponCode}' but found: [{string.Join(", ", coupons.Select(c => c.Code))}]");
    }
}
