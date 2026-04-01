using Reqnroll;

using WIMP.App.Services;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

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
