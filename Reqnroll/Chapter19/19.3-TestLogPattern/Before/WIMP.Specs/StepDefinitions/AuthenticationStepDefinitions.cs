using Reqnroll;

using WIMP.App.RestApi;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class AuthenticationStepDefinitions(AuthenticationApiDriver authApiDriver)
{
    private TestActionResult<LoginResponse> loginResult = TestActionResult<LoginResponse>.NotExecuted;

    [Given("the customer is authenticated")]
    public async Task GivenTheCustomerIsAuthenticated()
    {
        await authApiDriver.Login(DomainDefaults.CustomerName, DomainDefaults.Password).Execute();
    }

    [When("the customer attempts to log in with valid password")]
    public async Task WhenTheCustomerAttemptsToLogInWithValidPassword()
    {
        loginResult = await authApiDriver.Login(DomainDefaults.CustomerName, DomainDefaults.Password).AttemptExecute();
    }

    [Then("they should be authenticated")]
    public void ThenTheyShouldBeAuthenticated()
    {
        loginResult.AssertSucceeded();
    }
}
