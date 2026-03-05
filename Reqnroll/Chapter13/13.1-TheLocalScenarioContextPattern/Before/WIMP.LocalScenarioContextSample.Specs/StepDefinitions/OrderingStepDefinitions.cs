using Reqnroll;

using WIMP.LocalScenarioContextSample.App.Infrastructure;
using WIMP.LocalScenarioContextSample.App.Services;

namespace WIMP.LocalScenarioContextSample.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions
{
    [Given("the customer {word} has placed the order #{int}")]
    public void GivenTheCustomerHasPlacedTheOrder(string customerName, int orderNo)
    {
        OrderService.PlaceOrder(customerName, orderNo, "Margherita");
    }

    [When("the customer {word} cancels the order #{int}")]
    public void WhenTheCustomerCancelsTheOrder(string customerName, int orderNo)
    {
        OrderService.CancelOrder(customerName, orderNo);
    }

    [Then("the customer {word} should receive a notification about the cancellation")]
    public void ThenTheCustomerShouldReceiveANotification(string customerName)
    {
        Assert.IsTrue(NotificationService.WasNotificationSent(customerName));
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
