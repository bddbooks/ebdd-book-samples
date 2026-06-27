using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(AuthenticationContext authContext, OrderingApiDriver orderingApiDriver, NotificationsApiDriver notificationsApiDriver)
{
    private int? placedOrderNo;

    [Given("they have placed an order")]
    public async Task GivenTheyHavePlacedAnOrder()
    {
        var placeOrderRequest = new PlaceOrderRequestObjectMother().Build();
        var placedOrder = await orderingApiDriver.PlaceOrder(placeOrderRequest).Execute();
        placedOrderNo = placedOrder.OrderNo;
    }

    [When("they cancel the placed order")]
    public async Task WhenTheyCancelThePlacedOrder()
    {
        int orderNumber = placedOrderNo ?? throw new InvalidOperationException("No placed order");
        await orderingApiDriver.CancelOrder(orderNumber).Execute();
    }

    [Then("they should receive a notification about the cancellation")]
    public async Task ThenTheyShouldReceiveANotificationAboutTheCancellation()
    {
        string customerName = authContext.LoggedInCustomerName ?? throw new InvalidOperationException("No logged in customer name");
        var notifications = await notificationsApiDriver.GetNotifications(customerName).Execute();

        Assert.IsNotNull(notifications);
        Assert.IsTrue(notifications.Any(n =>
                n.Message.Contains("cancelled", StringComparison.OrdinalIgnoreCase)),
            "Expected a cancellation notification but none was found.");
    }
}
