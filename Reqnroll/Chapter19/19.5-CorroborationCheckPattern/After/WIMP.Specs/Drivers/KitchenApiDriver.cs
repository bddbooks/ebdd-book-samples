using Microsoft.Extensions.Logging;

using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class KitchenApiDriver(ILoggerFactory loggerFactory, RestApiContext restApiContext)
{
    public TestAction<Order> TakeNextOrder() =>
        new LambdaAction<Order>(loggerFactory, "Take next order", async () =>
            await restApiContext.ProcessRequest<Order>(
                "Take next order", HttpMethod.Post, "/api/kitchen/take-next-order"));
}
