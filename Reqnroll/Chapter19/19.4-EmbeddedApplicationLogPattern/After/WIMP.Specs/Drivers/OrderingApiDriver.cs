using System.Net;

using Microsoft.Extensions.Logging;

using WIMP.App.Models;
using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class OrderingApiDriver(ILoggerFactory loggerFactory, RestApiContext restApiContext)
{
    public TestAction<Order> PlaceOrder(PlaceOrderRequest placeOrderRequest) =>
        new LambdaAction<Order>(loggerFactory, "Place order", placeOrderRequest, async () =>
            await restApiContext.ProcessRequest<Order>(
                "Place order", HttpMethod.Post, "/api/orders",
                placeOrderRequest, HttpStatusCode.Created));
}
