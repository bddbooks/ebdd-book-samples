using Reqnroll;

using WIMP.ShareableScenarioContextSample.App.Services;
using WIMP.ShareableScenarioContextSample.Specs.Support;

namespace WIMP.ShareableScenarioContextSample.Specs.StepDefinitions;

[Binding]
public class AuthenticationStepDefinitions(AuthenticationContext authContext)
{
    [Given("the customer {word} has logged in")]
    public void GivenTheCustomerHasLoggedIn(string customerName)
    {
        AuthenticationService.Login(customerName);
        authContext.LoggedInCustomerName = customerName;
    }
}
