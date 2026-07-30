using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class MenuApiDriver(RestApiContext restApiContext)
{
    public TestAction<IReadOnlyCollection<OfferedMenuItem>> LoadMenu(string? promotionName = null) =>
        new LambdaAction<IReadOnlyCollection<OfferedMenuItem>>("Load menu", async () =>
            await restApiContext.GetRequest<IReadOnlyCollection<OfferedMenuItem>>(
                "/api/menu" + (promotionName != null ? $"?promo={promotionName}" : "")));
}
