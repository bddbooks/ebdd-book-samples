using Reqnroll;

using WIMP.App.Services;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class AuthenticationStepDefinitions
{
    [Given("the customer {word} has logged in")]
    public void GivenTheCustomerHasLoggedIn(string customerName)
    {
        AuthenticationService.Login(customerName);
    }
}
