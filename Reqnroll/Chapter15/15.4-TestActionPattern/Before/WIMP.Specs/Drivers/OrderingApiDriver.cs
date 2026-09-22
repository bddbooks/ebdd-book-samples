using System.Diagnostics;
using System.Net;

using WIMP.App.Models;
using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class OrderingApiDriver(RestApiContext restApiContext)
{
    public async Task<Order> PerformPlaceOrder(PlaceOrderRequest placeOrderRequest)
    {
        Console.WriteLine("Executing Place order...");
        var stopwatch = Stopwatch.StartNew();
        try
        {
            var response = await restApiContext.ProcessRequest<Order>(
                "Place order", HttpMethod.Post, "/api/orders",
                placeOrderRequest, HttpStatusCode.Created);
            Console.WriteLine(
                $"Place order executed successfully in {stopwatch.Elapsed}.");
            return response;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Place order failed: {ex.Message}");
            throw;
        }
    }

    public async Task PerformCancelOrder(int orderNumber)
    {
        Console.WriteLine("Executing Cancel order...");
        var stopwatch = Stopwatch.StartNew();
        try
        {
            await restApiContext.ProcessRequest<VoidReturn>(
                "Cancel order", HttpMethod.Delete, $"/api/orders/{orderNumber}",
                successStatusCode: HttpStatusCode.NoContent);
            Console.WriteLine(
                $"Cancel order executed successfully in {stopwatch.Elapsed}.");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Cancel order failed: {ex.Message}");
            throw;
        }
    }
}
