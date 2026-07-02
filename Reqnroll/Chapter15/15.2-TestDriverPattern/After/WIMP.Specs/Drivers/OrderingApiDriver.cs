using System.Net;
using System.Net.Http.Headers;
using System.Net.Http.Json;

using WIMP.App.Models;
using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class OrderingApiDriver(AppHostingContext appHostingContext, RestApiContext restApiContext)
{
    public async Task<Order> PerformPlaceOrder(PlaceOrderRequest placeOrderRequest)
    {
        var request = new HttpRequestMessage(HttpMethod.Post, "/api/orders");
        request.Headers.Authorization = new AuthenticationHeaderValue(
            "Bearer", restApiContext.BearerToken);
        request.Content = JsonContent.Create(placeOrderRequest);
        var response = await appHostingContext.AppHost.CreateClient()
            .SendAsync(request);

        if (response.StatusCode != HttpStatusCode.Created)
        {
            string? errorMessage =
                (await response.Content.ReadFromJsonAsync<ErrorResponse>())?.Error;
            throw new WimpActionFailedException(
                $"Place order failed with status code {response.StatusCode}. " +
                $"Error message: '{errorMessage}'");
        }

        return await response.Content.ReadFromJsonAsync<Order>()
               ?? throw new InvalidOperationException("No result payload found");
    }
    public async Task PerformCancelOrder(int orderNumber)
    {
        var request = new HttpRequestMessage(HttpMethod.Delete, $"/api/orders/{orderNumber}");
        request.Headers.Authorization = new AuthenticationHeaderValue(
            "Bearer", restApiContext.BearerToken);
        var response = await appHostingContext.AppHost.CreateClient()
            .SendAsync(request);

        if (response.StatusCode != HttpStatusCode.NoContent)
        {
            string? errorMessage =
                (await response.Content.ReadFromJsonAsync<ErrorResponse>())?.Error;
            throw new WimpActionFailedException(
                $"Place order failed with status code {response.StatusCode}. " +
                $"Error message: '{errorMessage}'");
        }
    }
}
