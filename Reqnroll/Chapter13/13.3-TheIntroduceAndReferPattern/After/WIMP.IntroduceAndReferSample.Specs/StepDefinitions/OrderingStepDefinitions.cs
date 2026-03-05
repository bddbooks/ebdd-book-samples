using Reqnroll;

using WIMP.IntroduceAndReferSample.App.Infrastructure;
using WIMP.IntroduceAndReferSample.App.Services;
using WIMP.IntroduceAndReferSample.Specs.Support;

namespace WIMP.IntroduceAndReferSample.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(
    OrderingContext orderingContext,
    AuthenticationContext authContext)
{
    [Given("the logged in customer has placed an order")]
    public void GivenTheLoggedInCustomerHasPlacedAnOrder()
    {
        var order = OrderService.PlaceOrder(
            authContext.LoggedInCustomerName, "Margherita");
        orderingContext.PlacedOrderNo = order.OrderNo;
    }

    [When("the logged in customer cancels {order}")]
    public void WhenTheLoggedInCustomerCancelsTheOrder(int orderNo)
    {
        OrderService.CancelOrder(
            authContext.LoggedInCustomerName,
            orderNo);
    }

    [Then("the logged in customer should receive a notification about the cancellation")]
    public void ThenTheLoggedInCustomerShouldReceiveANotification()
    {
        Assert.IsTrue(NotificationService.WasNotificationSent(authContext.LoggedInCustomerName));
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
