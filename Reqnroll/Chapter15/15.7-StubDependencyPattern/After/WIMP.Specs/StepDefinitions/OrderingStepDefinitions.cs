using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(TimeServiceDriver timeServiceDriver, OrderingApiDriver orderingApiDriver, NotificationsApiDriver notificationsApiDriver)
{
    public record OrderRequestData(TimeSpan ExpectedDeliveryTime);

    [Given("they have placed an order")]
    public async Task GivenTheyHavePlacedAnOrder(DataTable dataTable)
    {
        var orderData = dataTable.CreateInstance<OrderRequestData>();
        var expectedDeliveryTime = TimeOnly.FromTimeSpan(orderData.ExpectedDeliveryTime);
        // ensuring that the placing time is before the expected delivery time
        timeServiceDriver.SetCurrentTime(expectedDeliveryTime.Add(TimeSpan.FromMinutes(-5)));
        // preparing a place order request with expected delivery time (this setting is only available for testing) 
        var placeOrderRequest = new PlaceOrderRequestObjectMother()
            .WithExpectedDeliveryTime(timeServiceDriver.GetTodayTime(expectedDeliveryTime))
            .Build();
        await orderingApiDriver.PlaceOrder(placeOrderRequest).Execute();
    }

    [When("the delivery has not been made by {TimeOnly}")]
    public void WhenTheDeliveryHasNotBeenMadeBy(TimeOnly time)
    {
        timeServiceDriver.SetCurrentTime(time);
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
