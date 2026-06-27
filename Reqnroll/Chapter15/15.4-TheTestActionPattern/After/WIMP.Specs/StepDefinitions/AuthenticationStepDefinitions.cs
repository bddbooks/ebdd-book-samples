using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class AuthenticationStepDefinitions(AuthenticationContext authContext, AuthenticationApiDriver authApiDriver)
{
    private WimpActionFailedException? loginError;

    [Given("the customer has authenticated")]
    public async Task GivenTheCustomerHasAuthenticated()
    {
        await authApiDriver.Login(DomainDefaults.CustomerName, DomainDefaults.Password).Execute();
        authContext.LoggedInCustomerName = DomainDefaults.CustomerName;
    }

    [When("the customer attempts to log in with a wrong password")]
    public async Task WhenTheCustomerAttemptsToLogInWithAWrongPassword()
    {
        try
        {
            await authApiDriver.Login(DomainDefaults.CustomerName, DomainDefaults.WrongPassword).Execute();
            loginError = null;
        }
        catch (WimpActionFailedException ex)
        {
            loginError = ex;
        }
    }

    [Then("the login should fail with {string}")]
    public void ThenTheLoginShouldFailWith(string expectedMessage)
    {
        Assert.IsNotNull(loginError, "Login should fail");
        StringAssert.Contains(loginError.Message, expectedMessage,
            "Login should fail with the right error message");
    }
}
