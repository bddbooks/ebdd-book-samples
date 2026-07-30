using Reqnroll;

using WIMP.App.Models;
using WIMP.App.Services;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(OrderingContext orderingContext, OrderService orderService)
{
    [When("the customer places an order for {int} pizza(s) of size {pizza-size}")]
    public void WhenTheCustomerPlacesAnOrderFor(int count, PizzaSize pizzaSize)
    {
        var order = new OrderObjectMother()
            .WithItems(count, size: pizzaSize)
            .Build();

        AuthenticationService.Login(DomainDefaults.CustomerName);
        var placedOrder = orderService.PlaceOrder(order);
        orderingContext.CurrentOrderNo = placedOrder.OrderNo;
    }

    #region Additional Step Definitions

    [Then("the order should be rejected")]
    public void ThenTheOrderShouldBeRejected()
    {
        int orderNo = orderingContext.CurrentOrderNo ?? throw new InvalidOperationException("No current order");
        var order = orderService.GetOrder(orderNo) ?? throw new InvalidOperationException("Order not found");
        Assert.AreEqual(OrderStatus.Rejected, order.Status);
    }

    #endregion
}
