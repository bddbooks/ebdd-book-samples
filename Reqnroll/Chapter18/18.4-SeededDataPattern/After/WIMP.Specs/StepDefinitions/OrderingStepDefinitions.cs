using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, AuthenticationApiDriver authApiDriver)
{
    private int? placedOrderNo;

    [Given("the customer has placed an order containing a {string} pizza")]
    public async Task GivenTheCustomerHasPlacedAnOrderContainingAPizza(string pizzaName)
    {
        await authApiDriver
            .Login(DomainDefaults.CustomerName, DomainDefaults.Password)
            .Execute();
        var orderRequest = new PlaceOrderRequestObjectMother()
            .WithItem(name: pizzaName)
            .Build();
        var placedOrder = await orderingApiDriver.PlaceOrder(orderRequest)
            .Execute();
        placedOrderNo = placedOrder.OrderNo;
    }

    [When("the order is delivered")]
    public async Task WhenTheOrderIsDelivered()
    {
        await orderingApiDriver.DeliverOrder(placedOrderNo ?? throw new InvalidOperationException("Order not placed"))
            .Execute();
    }
}
