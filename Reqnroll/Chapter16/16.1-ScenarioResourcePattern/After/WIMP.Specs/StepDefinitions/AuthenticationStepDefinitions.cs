using Reqnroll;

using WIMP.App.RestApi;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class AuthenticationStepDefinitions(AuthenticationApiDriver authApiDriver)
{
    private TestActionResult<LoginResponse> loginResult = TestActionResult<LoginResponse>.NotExecuted;

    [When("the customer attempts to log in with a wrong password")]
    public async Task WhenTheCustomerAttemptsToLogInWithAWrongPassword()
    {
        loginResult = await authApiDriver.Login(DomainDefaults.CustomerName, DomainDefaults.WrongPassword).AttemptExecute();
    }

    [Then("the login should fail with {string}")]
    public void ThenTheLoginShouldFailWith(string expectedMessage)
    {
        loginResult.AssertFailedWithErrorMessageContains(expectedMessage);
    }
}
