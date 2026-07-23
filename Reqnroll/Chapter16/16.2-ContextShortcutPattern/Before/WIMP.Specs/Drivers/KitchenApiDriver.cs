using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class KitchenApiDriver(RestApiContext restApiContext)
{
    public TestAction<Order> TakeNextOrder() =>
        new LambdaAction<Order>("Take next order", async () =>
            await restApiContext.ProcessRequest<Order>(
                "Take next order", HttpMethod.Post, "/api/kitchen/take-next-order"));

    public TestAction<VoidReturn> SetReady(int orderNo) =>
        new LambdaAction("Order ready to pick up", async () =>
            await restApiContext.ProcessRequest<VoidReturn>(
                "Order ready to pick up", HttpMethod.Post, $"/api/orders/{orderNo}/ready-for-pickup"));
}
