using WIMP.App.Models;
using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class BackdoorApiDriver(RestApiContext restApiContext)
{
    public TestAction<Order> PrepareOrder(string customerName, PlaceOrderRequest orderRequest, OrderStatus status) =>
        new LambdaAction<Order>("Prepare test order", async () =>
            await restApiContext.ProcessRequest<Order>(
                "Prepare test order", HttpMethod.Post, $"/api/test/prepare-order?customerName={customerName}&status={status}",
                orderRequest));
}
