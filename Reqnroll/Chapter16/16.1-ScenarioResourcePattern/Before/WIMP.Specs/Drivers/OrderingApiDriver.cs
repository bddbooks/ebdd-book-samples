using System.Net;

using WIMP.App.Models;
using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class OrderingApiDriver(RestApiContext restApiContext)
{
    public TestAction<Order> PlaceOrder(PlaceOrderRequest placeOrderRequest) =>
        new LambdaAction<Order>("Place order", async () =>
            await restApiContext.ProcessRequest<Order>(
                "Place order", HttpMethod.Post, "/api/orders",
                placeOrderRequest, HttpStatusCode.Created));

    public TestAction<VoidReturn> CancelOrder(int orderNumber) =>
        new LambdaAction("Cancel order", async () =>
            await restApiContext.ProcessRequest<VoidReturn>(
                "Cancel order", HttpMethod.Delete, $"/api/orders/{orderNumber}",
                successStatusCode: HttpStatusCode.NoContent));
}
