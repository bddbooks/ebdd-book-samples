using Reqnroll;

using WIMP.App.Infrastructure;
using WIMP.App.Services;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(
    OrderingContext orderingContext,
    AuthenticationContext authContext)
{
    [Given("the authenticated customer has placed an order")]
    public void GivenTheAuthenticatedCustomerHasPlacedAnOrder()
    {
        var order = OrderService.PlaceOrder(
            authContext.AuthenticatedCustomerName, "Margherita");
        orderingContext.CurrentOrderNo = order.OrderNo;
    }

    [When("the authenticated customer cancels {order}")]
    public void WhenTheAuthenticatedCustomerCancelsTheOrder(int orderNo)
    {
        OrderService.CancelOrder(
            authContext.AuthenticatedCustomerName,
            orderNo);
    }

    [Then("the authenticated customer should receive a notification about the cancellation")]
    public void ThenTheAuthenticatedCustomerShouldReceiveANotification()
    {
        Assert.IsTrue(NotificationService.WasNotificationSent(authContext.AuthenticatedCustomerName));
    }

    #region Reset database for every scenario execution

    /// <summary>
    /// This hook resets the in-memory database before each scenario execution,
    /// ensuring that each test starts with a clean state.
    /// The pattern 15.2 contains a better approach for in-memory database management via dependencies.
    /// The pattern 18.2 contains further options for dealing with shared resources, such as using a real database.
    /// </summary>
    [BeforeScenario]
    public void ResetDatabase()
    {
        DataContext.Instance.Reset();
    }

    #endregion
}
