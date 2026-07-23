using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(OrderingContext orderingContext, AuthenticationApiDriver authApiDriver, OrderingApiDriver orderingApiDriver)
{
    [Given("an authenticated customer has placed an order")]
    public async Task GivenAnAuthenticatedCustomerHasPlacedAnOrder()
    {
        await authApiDriver.Login(DomainDefaults.CustomerName, DomainDefaults.Password).Execute();
        var placeOrderRequest = new PlaceOrderRequestObjectMother().Build();
        var placedOrder = await orderingApiDriver.PlaceOrder(placeOrderRequest).Execute();
        orderingContext.PlacedOrderNo = placedOrder.OrderNo;
    }
}
