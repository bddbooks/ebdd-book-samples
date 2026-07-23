using System.Net;

using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class OrderingApiDriver(RestApiContext restApiContext)
{
    public TestAction<Order> GetOrder(int orderNo) =>
        new LambdaAction<Order>("Get order", async () =>
            await restApiContext.ProcessRequest<Order>(
                "Get order", HttpMethod.Get, $"/api/orders/{orderNo}"));

    public TestAction<VoidReturn> CancelOrder(int orderNo) =>
        new LambdaAction("Cancel order", async () =>
            await restApiContext.ProcessRequest<VoidReturn>(
                "Cancel order", HttpMethod.Delete, $"/api/orders/{orderNo}",
                successStatusCode: HttpStatusCode.NoContent));
}
