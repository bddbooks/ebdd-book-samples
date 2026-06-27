using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, NotificationsApiDriver notificationsApiDriver)
{
    public record OrderRequestData(TimeSpan ExpectedDeliveryTime);

    [Given("they have placed an order")]
    public async Task GivenTheyHavePlacedAnOrder(DataTable dataTable)
    {
        var orderData = dataTable.CreateInstance<OrderRequestData>();
        var expectedDeliveryTime = TimeOnly.FromTimeSpan(orderData.ExpectedDeliveryTime);

        // With the real time service we cannot fast-forward time, so cannot use the specified
        // expectedDeliveryTime. Instead, we force the expected delivery time being in 0.5 seconds,
        // and we wait in the WhenTheDeliveryHasNotBeenMadeBy method for the background timer loop
        // to process the subscription.
        var placeOrderRequest = new PlaceOrderRequestObjectMother()
            .WithExpectedDeliveryTime(DateTimeOffset.Now.AddSeconds(0.5))
            .Build();

        await orderingApiDriver.PlaceOrder(placeOrderRequest).Execute();
    }

    [When("the delivery has not been made by {TimeOnly}")]
    public void WhenTheDeliveryHasNotBeenMadeBy(TimeOnly time)
    {
        //Workaround: see notes above!
        Thread.Sleep(TimeSpan.FromMilliseconds(1500));
    }

    [Then("the customer should receive a notification about the delay")]
    public async Task ThenTheCustomerShouldReceiveANotificationAboutTheDelay()
    {
        var notifications = await notificationsApiDriver.GetNotifications(DomainDefaults.CustomerName).Execute();

        Assert.IsNotNull(notifications);
        Assert.IsTrue(notifications.Any(n =>
                n.Message.Contains("delayed", StringComparison.OrdinalIgnoreCase)),
            "Expected a delay notification but none was found.");
    }
}
