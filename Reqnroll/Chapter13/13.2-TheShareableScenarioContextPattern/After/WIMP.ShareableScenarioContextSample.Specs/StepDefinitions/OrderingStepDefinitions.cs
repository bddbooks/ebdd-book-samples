using Reqnroll;

using WIMP.ShareableScenarioContextSample.App.Infrastructure;
using WIMP.ShareableScenarioContextSample.App.Services;
using WIMP.ShareableScenarioContextSample.Specs.Support;

namespace WIMP.ShareableScenarioContextSample.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(AuthenticationContext authContext)
{
    private int placedOrderNo;

    [Given("the logged in customer has placed the order #{int}")]
    public void GivenTheLoggedInCustomerHasPlacedTheOrder(int orderNo)
    {
        OrderService.PlaceOrder(authContext.LoggedInCustomerName, orderNo, "Margherita");
        placedOrderNo = orderNo;
    }

    [When("the logged in customer cancels the placed order")]
    public void WhenTheLoggedInCustomerCancelsThePlacedOrder()
    {
        OrderService.CancelOrder(authContext.LoggedInCustomerName, placedOrderNo);
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
