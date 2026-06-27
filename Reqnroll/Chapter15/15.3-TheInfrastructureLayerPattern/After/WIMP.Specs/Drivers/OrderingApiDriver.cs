using System.Net;

using WIMP.App.Models;
using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class OrderingApiDriver(RestApiContext restApiContext)
{
    public async Task<Order> PerformPlaceOrder(PlaceOrderRequest placeOrderRequest)
    {
        return await restApiContext.ProcessRequest<Order>(
            "Place order",
            HttpMethod.Post,
            "/api/orders",
            placeOrderRequest,
            HttpStatusCode.Created);
    }

    public async Task PerformCancelOrder(int orderNumber)
    {
        await restApiContext.ProcessRequest<VoidReturn>(
            "Cancel order",
            HttpMethod.Delete,
            $"/api/orders/{orderNumber}",
            successStatusCode: HttpStatusCode.NoContent);
    }
}
