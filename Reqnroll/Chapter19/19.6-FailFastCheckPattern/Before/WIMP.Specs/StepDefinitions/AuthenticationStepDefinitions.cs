using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class AuthenticationStepDefinitions(AuthenticationApiDriver authApiDriver)
{
    [Given("the restaurant owner is authenticated")]
    public async Task GivenTheRestaurantOwnerIsAuthenticated()
    {
        await authApiDriver
            .Login(DomainDefaults.RestaurantOwner, DomainDefaults.Password)
            .Execute();
    }
}
