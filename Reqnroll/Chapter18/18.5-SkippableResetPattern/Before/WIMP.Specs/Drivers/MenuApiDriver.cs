using System.Net;

using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class MenuApiDriver(RestApiContext restApiContext)
{
    public TestAction<IReadOnlyCollection<OfferedMenuItem>> LoadMenu(string? promotionName = null) =>
        new LambdaAction<IReadOnlyCollection<OfferedMenuItem>>("Load menu", async () =>
            await restApiContext.GetRequest<IReadOnlyCollection<OfferedMenuItem>>(
                "/api/menu" + (promotionName != null ? $"?promo={promotionName}" : "")));

    public TestAction<VoidReturn> AddMenuItem(MenuItem menuItem) =>
        new LambdaAction("Add menu item", async () =>
            await restApiContext.ProcessRequest<VoidReturn>(
                "Add menu item", HttpMethod.Post, "/api/menu",
                menuItem, HttpStatusCode.Created));
}
