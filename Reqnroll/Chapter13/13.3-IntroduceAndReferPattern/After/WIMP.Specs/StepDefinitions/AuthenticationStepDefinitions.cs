using Reqnroll;

using WIMP.App.Services;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class AuthenticationStepDefinitions(AuthenticationContext authContext)
{
    [Given("the customer {string} is authenticated")]
    public void GivenTheCustomerIsAuthenticated(string customerName)
    {
        AuthenticationService.Login(customerName);
        authContext.AuthenticatedCustomerName = customerName;
    }
}
