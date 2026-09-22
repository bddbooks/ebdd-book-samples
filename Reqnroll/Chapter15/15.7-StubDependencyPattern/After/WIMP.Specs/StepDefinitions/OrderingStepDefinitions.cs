using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(TimeServiceDriver timeServiceDriver, OrderingApiDriver orderingApiDriver)
{
    public record OrderRequestData(TimeSpan ExpectedDeliveryTime);

    [Given("they have placed an order with")]
    public async Task GivenTheyHavePlacedAnOrderWith(DataTable orderDataTable)
    {
        var orderData = orderDataTable.CreateInstance<OrderRequestData>();
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
}
