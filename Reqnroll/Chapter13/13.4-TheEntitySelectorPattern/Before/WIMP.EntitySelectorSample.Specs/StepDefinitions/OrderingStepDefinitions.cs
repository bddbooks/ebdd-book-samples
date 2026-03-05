using Reqnroll;

using WIMP.EntitySelectorSample.App.Infrastructure;
using WIMP.EntitySelectorSample.App.Services;
using WIMP.EntitySelectorSample.Specs.Support;

namespace WIMP.EntitySelectorSample.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(OrderingContext orderingContext)
{
    [Given("the following orders have been placed")]
    public void GivenTheFollowingOrdersHaveBeenPlaced(Table ordersTable)
    {
        // In order to ensure the orders, we replay the ordering steps with a test customer.
        // A better approach to ensure this context is shown in Chapter 15, TODO pattern.
        AuthenticationService.Login("Rebecca");
        foreach (var row in ordersTable.Rows)
        {
            // A better way of processing data tables is shown in Chapter 14, Data table accessor pattern.
            int? orderNo = ordersTable.ContainsColumn("Order number") ? int.Parse(row["Order number"]) : null;
            var placingTime = TimeSpan.Parse(row["Placed At"]);

            var order = OrderService.PlaceOrder("Rebecca", "Margherita", placingTime, orderNo);
            orderingContext.PlacedOrders.Add(order);
        }
    }

    [When("a kitchen staff member asks for an order to work on")]
    public void WhenKitchenStaffAsksForOrder()
    {
        var order = OrderService.StartWorkOnNextOrder();
        orderingContext.TakenOrderNo = order?.OrderNo;
    }

    [Then("{order} should be taken")]
    public void ThenTheOrderShouldBeTaken(int orderNo)
    {
        Assert.IsNotNull(orderingContext.TakenOrderNo);
        Assert.AreEqual(orderNo, orderingContext.TakenOrderNo);
    }

    #region Reset database for every scenario execution

    /// <summary>
    /// This hook resets the in-memory database before each scenario execution,
    /// ensuring that each test starts with a clean state.
    /// The pattern TODO1 contains a better approach for in-memory database management via dependencies.
    /// The pattern TODO2 contains further options for dealing with shared resources, such as using a real database.
    /// </summary>
    [BeforeScenario]
    public void ResetDatabase()
    {
        DataContext.Instance.Reset();
    }

    #endregion
}
