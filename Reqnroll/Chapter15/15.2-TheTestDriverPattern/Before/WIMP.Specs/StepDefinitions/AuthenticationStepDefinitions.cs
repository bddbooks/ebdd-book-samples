using System.Net;
using System.Net.Http.Json;

using Reqnroll;

using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class AuthenticationStepDefinitions(AuthenticationContext authContext, RestApiContext restApiContext, AppHostingContext appHostingContext)
{
    private HttpResponseMessage? loginApiResponse;

    [Given("the customer has authenticated")]
    public async Task GivenTheCustomerHasAuthenticated()
    {
        var payload = new LoginRequest(
            DomainDefaults.CustomerName,
            DomainDefaults.Password);
        var response = await appHostingContext.AppHost.CreateClient()
            .PostAsJsonAsync("/api/auth/login", payload);

        Assert.AreEqual(HttpStatusCode.OK, response.StatusCode);
        var loginResponse = await response.Content.ReadFromJsonAsync<LoginResponse>()
                            ?? throw new InvalidOperationException("No result payload found");
        restApiContext.BearerToken = loginResponse.Token;
        authContext.LoggedInCustomerName = DomainDefaults.CustomerName;
    }

    [When("the customer attempts to log in with a wrong password")]
    public async Task WhenTheCustomerAttemptsToLogInWithAWrongPassword()
    {
        var payload = new LoginRequest(
            DomainDefaults.CustomerName,
            DomainDefaults.WrongPassword);
        loginApiResponse = await appHostingContext.AppHost.CreateClient()
            .PostAsJsonAsync("/api/auth/login", payload);
    }

    [Then("the login should fail with {string}")]
    public async Task ThenTheLoginShouldFailWith(string expectedMessage)
    {
        Assert.IsNotNull(loginApiResponse);
        Assert.AreNotEqual(HttpStatusCode.OK, loginApiResponse.StatusCode, "Login should fail");
        string errorMessage = await loginApiResponse.Content.ReadAsStringAsync();
        StringAssert.Contains(errorMessage, expectedMessage,
            "Login should fail with the right error message");
    }
}
