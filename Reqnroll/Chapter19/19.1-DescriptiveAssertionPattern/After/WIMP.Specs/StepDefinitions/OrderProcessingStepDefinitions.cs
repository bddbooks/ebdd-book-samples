using AwesomeAssertions;

using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderProcessingStepDefinitions(AuthenticationApiDriver authApiDriver, KitchenApiDriver kitchenApiDriver, BackdoorApiDriver backdoorApiDriver)
{
    private readonly List<Order> placedOrders = [];
    private Order? takenOrder;

    [Given("the following orders have been placed")]
    public async Task GivenTheFollowingOrdersHaveBeenPlaced(OrderData[] orders)
    {
        foreach (var orderData in orders)
        {
            var orderRequest = new PlaceOrderRequestObjectMother()
                .WithPlacingTime(orderData.PlacedAt)
                .Build();
            var placedOrder = await backdoorApiDriver
                .PrepareOrder(DomainDefaults.CustomerName, orderRequest, OrderStatus.Placed)
                .Execute();
            placedOrders.Add(placedOrder);
        }
    }

    [When("a kitchen staff member asks for an order to work on")]
    public async Task WhenAKitchenStaffMemberAsksForAnOrderToWorkOn()
    {
        await authApiDriver.Login(DomainDefaults.KitchenStaff, DomainDefaults.Password).Execute();
        takenOrder = await kitchenApiDriver.TakeNextOrder().Execute();
    }

    [Then("the earliest order received should be taken")]
    public void ThenTheEarliestOrderReceivedShouldBeTaken()
    {
        takenOrder.Should().NotBeNull(because: "an order should have been taken");
        //with MsTest assertion API:
        //  Assert.IsNotNull(takenOrder, "Order was not taken");

        var earliestOrder = placedOrders.OrderBy(o => o.PlacingTime).First();

        takenOrder.Should().Be(earliestOrder,
            OrderNumberComparer.Value, because: "the earliest order is expected");
        //with MsTest assertion API:
        //  Assert.AreEqual(earliestOrder.OrderNo, takenOrder.OrderNo,
        //      $"Expected the earliest order #{earliestOrder.OrderNo} (placed at {earliestOrder.PlacingTime}) but got order #{takenOrder.OrderNo} (placed at {takenOrder.PlacingTime})");
    }
}
