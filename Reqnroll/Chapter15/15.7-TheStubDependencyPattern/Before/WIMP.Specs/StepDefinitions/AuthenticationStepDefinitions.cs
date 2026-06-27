using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class AuthenticationStepDefinitions(AuthenticationApiDriver authApiDriver)
{
    [Given("the customer has authenticated")]
    public async Task GivenTheCustomerHasAuthenticated()
    {
        await authApiDriver.Login(DomainDefaults.CustomerName, DomainDefaults.Password).Execute();
    }
}
