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

    public TestAction<Order> DeliverOrder(int orderNo) =>
        new LambdaAction<Order>("Deliver order", async () =>
            await restApiContext.ProcessRequest<Order>(
                "Deliver order", HttpMethod.Post, $"/api/orders/{orderNo}/deliver"));
}
