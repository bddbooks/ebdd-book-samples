using System.Net;
using System.Net.Http.Headers;
using System.Net.Http.Json;

using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(AuthenticationContext authContext, RestApiContext restApiContext, AppHostingContext appHostingContext)
{
    private int? placedOrderNo;

    [Given("they have placed an order")]
    public async Task GivenTheyHavePlacedAnOrder()
    {
        var placeOrderRequest = new PlaceOrderRequestObjectMother().Build();
        var request = new HttpRequestMessage(HttpMethod.Post, "/api/orders");
        request.Headers.Authorization = new AuthenticationHeaderValue(
            "Bearer", restApiContext.BearerToken);
        request.Content = JsonContent.Create(placeOrderRequest);
        var response = await appHostingContext.AppHost.CreateClient()
            .SendAsync(request);

        Assert.AreEqual(HttpStatusCode.Created, response.StatusCode, $"Place order failed with status code {response.StatusCode}.");

        var placedOrder = await response.Content.ReadFromJsonAsync<Order>()
                    ?? throw new InvalidOperationException("No result payload found");

        placedOrderNo = placedOrder.OrderNo;
    }

    [When("they cancel the placed order")]
    public async Task WhenTheyCancelThePlacedOrder()
    {
        int orderNumber = placedOrderNo ?? throw new InvalidOperationException("No placed order");
        var request = new HttpRequestMessage(HttpMethod.Delete, $"/api/orders/{orderNumber}");
        request.Headers.Authorization = new AuthenticationHeaderValue(
            "Bearer", restApiContext.BearerToken);
        var response = await appHostingContext.AppHost.CreateClient()
            .SendAsync(request);

        Assert.AreEqual(HttpStatusCode.NoContent, response.StatusCode, $"Place order failed with status code {response.StatusCode}.");
    }

    [Then("they should receive a notification about the cancellation")]
    public async Task ThenTheyShouldReceiveANotificationAboutTheCancellation()
    {
        string customerName = authContext.LoggedInCustomerName ?? throw new InvalidOperationException("No logged in customer name");
        var notifications = await appHostingContext.AppHost.CreateClient()
            .GetFromJsonAsync<Notification[]>($"/api/notifications/{customerName}")
            ?? throw new InvalidOperationException("No result payload found");

        Assert.IsNotNull(notifications);
        Assert.IsTrue(notifications.Any(n =>
                n.Message.Contains("cancelled", StringComparison.OrdinalIgnoreCase)),
            "Expected a cancellation notification but none was found.");
    }
}
