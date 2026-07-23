using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class AuthenticationStepDefinitions(AuthenticationApiDriver authApiDriver)
{
    [Given("they have logged in")]
    public async Task GivenTheyHaveLoggedIn()
    {
        await authApiDriver
            .Login(DomainDefaults.CustomerName, DomainDefaults.Password)
            .Execute();
    }

    [Given("the restaurant owner is logged in")]
    public async Task GivenTheRestaurantOwnerIsLoggedIn()
    {
        await authApiDriver
            .Login(DomainDefaults.RestaurantOwner, DomainDefaults.Password)
            .Execute();
    }
}
