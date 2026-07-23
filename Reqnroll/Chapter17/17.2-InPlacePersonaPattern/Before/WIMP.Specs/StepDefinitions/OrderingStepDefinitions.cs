using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(
    OrderingApiDriver orderingApiDriver,
    BackdoorApiDriver backdoorApiDriver)
{
    [Given("the customer has the following orders")]
    public async Task GivenTheCustomerHasTheFollowingOrders(DataTable ordersTable)
    {
        var orders = ordersTable.CreateSet<OrderByNumberData>().ToList();

        foreach (var order in orders)
        {
            var orderRequest = new PlaceOrderRequestObjectMother().Build();
            await backdoorApiDriver
                .PrepareOrder(DomainDefaults.CustomerName, orderRequest, order.Status)
                .Execute();
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
        var expectedOrders = expectedOrdersTable.CreateSet<OrderByNumberData>().ToList();
        foreach (var expectedOrder in expectedOrders)
        {
            var order = await orderingApiDriver.GetOrder(expectedOrder.OrderNo).Execute();
            Assert.AreEqual(expectedOrder.Status, order.Status,
                $"Unexpected status for order {expectedOrder.OrderNo}.");
        }
    }
}

public class OrderByNumberData
{
    public int OrderNo { get; set; }
    public OrderStatus Status { get; set; }
}
