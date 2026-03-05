using Reqnroll;

using WIMP.LocalScenarioContextSample.App.Services;

namespace WIMP.LocalScenarioContextSample.Specs.StepDefinitions;

[Binding]
public class AuthenticationStepDefinitions
{
    [Given("the customer {word} has logged in")]
    public void GivenTheCustomerHasLoggedIn(string customerName)
    {
        AuthenticationService.Login(customerName);
    }
}
