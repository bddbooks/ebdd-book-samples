using Reqnroll;

using WIMP.ShareableScenarioContextSample.App.Services;

namespace WIMP.ShareableScenarioContextSample.Specs.StepDefinitions;

[Binding]
public class AuthenticationStepDefinitions
{
    [Given("the customer {word} has logged in")]
    public void GivenTheCustomerHasLoggedIn(string customerName)
    {
        AuthenticationService.Login(customerName);
    }
}
