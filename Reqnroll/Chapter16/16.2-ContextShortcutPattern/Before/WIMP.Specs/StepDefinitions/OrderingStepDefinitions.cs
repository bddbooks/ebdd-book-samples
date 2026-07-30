using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(OrderingContext orderingContext, AuthenticationApiDriver authApiDriver,
    OrderingApiDriver orderingApiDriver, KitchenApiDriver kitchenApiDriver)
{
    [Given("the customer has an order that is waiting for pickup")]
    public async Task GivenTheCustomerHasAnOrderThatIsWaitingForPickup()
    {
        await authApiDriver
            .Login(DomainDefaults.CustomerName, DomainDefaults.Password)
            .Execute();
        var orderRequest = new PlaceOrderRequestObjectMother().Build();
        var placedOrder = await orderingApiDriver.PlaceOrder(orderRequest)
            .Execute();
        await authApiDriver
            .Login(DomainDefaults.KitchenStaff, DomainDefaults.Password)
            .Execute();

        Order? takenOrder = null;
        while (takenOrder?.OrderNo != placedOrder.OrderNo)
        {
            takenOrder = await kitchenApiDriver.TakeNextOrder().Execute();
        }

        await kitchenApiDriver.SetReady(placedOrder.OrderNo)
            .Execute();
        orderingContext.CurrentOrderNo = placedOrder.OrderNo;
    }
}
