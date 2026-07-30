using System.Net;

using WIMP.App.Models;
using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class PromotionsApiDriver(RestApiContext restApiContext)
{
    public TestAction<Promotion> ActivatePromotion(ActivatePromotionRequest activatePromotionRequest) =>
        new LambdaAction<Promotion>("Activate promotion", async () =>
            await restApiContext.ProcessRequest<Promotion>(
                "Activate promotion", HttpMethod.Post, "/api/promotions",
                activatePromotionRequest, HttpStatusCode.Created));

    public TestAction<IReadOnlyCollection<Coupon>> GetCoupons(string customerEmail) =>
        new LambdaAction<IReadOnlyCollection<Coupon>>("Get coupons", async () =>
            await restApiContext.GetRequest<IReadOnlyCollection<Coupon>>(
                $"/api/coupons/{customerEmail}"));
}
