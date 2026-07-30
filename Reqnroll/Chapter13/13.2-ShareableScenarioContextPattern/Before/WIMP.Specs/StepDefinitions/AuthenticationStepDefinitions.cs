using Reqnroll;

using WIMP.App.Services;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class AuthenticationStepDefinitions
{
    [Given("the customer {string} is authenticated")]
    public void GivenTheCustomerIsAuthenticated(string customerName)
    {
        AuthenticationService.Login(customerName);
    }
}
