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

    public TestAction<VoidReturn> ChangeDeliveryAddress(int orderNo, ChangeAddressRequest changeAddressRequest) =>
        new LambdaAction("Change delivery address", async () =>
            await restApiContext.ProcessRequest<VoidReturn>(
                "Change delivery address", HttpMethod.Put, $"/api/orders/{orderNo}/delivery-address",
                changeAddressRequest));
}
