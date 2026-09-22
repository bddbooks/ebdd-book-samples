using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(OrderingApiDriver orderingApiDriver)
{
    public record OrderRequestData(TimeSpan ExpectedDeliveryTime);

    [Given("they have placed an order with")]
    public async Task GivenTheyHavePlacedAnOrderWith(DataTable orderDataTable)
    {
        var orderData = orderDataTable.CreateInstance<OrderRequestData>();
        var expectedDeliveryTime = TimeOnly.FromTimeSpan(orderData.ExpectedDeliveryTime);
        //NOTE: The expectedDeliveryTime is not used, because of the workaround we apply. It will be used once the pattern is applied.

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
        //WORKAROUND: see notes above!
        //NOTE: The time is not used, because of the workaround we apply. It will be used once the pattern is applied.
        Thread.Sleep(TimeSpan.FromMilliseconds(2000));
    }
}
