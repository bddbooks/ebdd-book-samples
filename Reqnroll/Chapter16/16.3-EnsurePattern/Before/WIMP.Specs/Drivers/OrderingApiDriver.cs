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

    public TestAction<OrderCollectionDetails> SetForCollection(int orderNo) =>
        new LambdaAction<OrderCollectionDetails>("Set for customer-collection", async () =>
            await restApiContext.ProcessRequest<OrderCollectionDetails>(
                "Set for customer-collection", HttpMethod.Put, $"/api/orders/{orderNo}/customer-collection"));
}
