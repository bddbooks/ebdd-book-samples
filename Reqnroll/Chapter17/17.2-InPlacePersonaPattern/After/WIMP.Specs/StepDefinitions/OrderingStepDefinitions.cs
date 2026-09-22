using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(
    OrderingContext orderingContext,
    OrderingApiDriver orderingApiDriver,
    BackdoorApiDriver backdoorApiDriver)
{
    [Given("the customer has the following orders")]
    public async Task GivenTheCustomerHasTheFollowingOrders(DataTable ordersTable)
    {
        var orders = ordersTable.CreateSet<NamedOrderData>().ToList();
        foreach (var order in orders)
        {
            var orderRequest = new PlaceOrderRequestObjectMother().Build();
            var placedOrder = await backdoorApiDriver
                .PrepareOrder(DomainDefaults.CustomerName, orderRequest, order.Status)
                .Execute();
            orderingContext.NamedOrders[order.OrderName] = placedOrder.OrderNo;
        }
    }

    [When("they cancel {order}")]
    public async Task WhenTheyCancelOrder(int orderNo)
    {
        await orderingApiDriver.CancelOrder(orderNo).Execute();
    }

    [Then("their order list should contain")]
    public async Task ThenTheirOrderListShouldContain(DataTable expectedOrdersTable)
    {
        var expectedOrders = expectedOrdersTable.CreateSet<NamedOrderData>().ToList();
        foreach (var expectedOrder in expectedOrders)
        {
            if (!orderingContext.NamedOrders.TryGetValue(expectedOrder.OrderName, out int orderNo))
            {
                throw new InvalidOperationException($"Order {expectedOrder.OrderName} not known");
            }

            var order = await orderingApiDriver.GetOrder(orderNo).Execute();
            Assert.AreEqual(expectedOrder.Status, order.Status,
                $"Unexpected status for order {expectedOrder.OrderName}.");
        }
    }
}
