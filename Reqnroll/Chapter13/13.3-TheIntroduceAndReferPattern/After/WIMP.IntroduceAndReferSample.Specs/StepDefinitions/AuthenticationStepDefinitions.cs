using Reqnroll;

using WIMP.IntroduceAndReferSample.App.Services;
using WIMP.IntroduceAndReferSample.Specs.Support;

namespace WIMP.IntroduceAndReferSample.Specs.StepDefinitions;

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
