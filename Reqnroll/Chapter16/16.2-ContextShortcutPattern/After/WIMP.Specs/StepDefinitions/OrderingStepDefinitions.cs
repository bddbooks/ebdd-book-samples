using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(OrderingContext orderingContext, BackdoorApiDriver backdoorApiDriver)
{
    [Given("the customer has an order that is waiting for pickup")]
    public async Task GivenTheCustomerHasAnOrderThatIsWaitingForPickup()
    {
        var orderRequest = new PlaceOrderRequestObjectMother().Build();
        var placedOrder = await backdoorApiDriver
            .PrepareOrder(DomainDefaults.CustomerName, orderRequest, OrderStatus.WaitingForPickup)
            .Execute();
        orderingContext.CurrentOrderNo = placedOrder.OrderNo;
    }
}
