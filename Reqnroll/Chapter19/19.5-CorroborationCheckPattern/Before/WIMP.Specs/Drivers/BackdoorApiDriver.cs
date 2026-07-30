using Microsoft.Extensions.Logging;

using WIMP.App.Models;
using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class BackdoorApiDriver(ILoggerFactory loggerFactory, RestApiContext restApiContext)
{
    public TestAction<Order> PrepareOrder(string customerName, PlaceOrderRequest orderRequest, OrderStatus status) =>
        new LambdaAction<Order>(loggerFactory, "Prepare test order", (customerName, orderRequest, status), async () =>
            await restApiContext.ProcessRequest<Order>(
                "Prepare test order", HttpMethod.Post, $"/api/test/prepare-order?customerName={customerName}&status={status}",
                orderRequest));
}
