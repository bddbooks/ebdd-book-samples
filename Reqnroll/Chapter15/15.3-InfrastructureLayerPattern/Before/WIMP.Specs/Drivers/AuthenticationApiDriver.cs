using System.Net;
using System.Net.Http.Json;

using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class AuthenticationApiDriver(AppHostingContext appHostingContext, RestApiContext restApiContext)
{
    public async Task PerformLogin(string customerName, string password)
    {
        var payload = new LoginRequest(customerName, password);

        var response = await appHostingContext.AppHost.CreateClient()
            .PostAsJsonAsync("/api/auth/login", payload);

        if (response.StatusCode != HttpStatusCode.OK)
        {
            string errorMessage = await response.Content.ReadAsStringAsync();
            throw new WimpActionFailedException(
                $"Login failed with status code {response.StatusCode}. Error message: '{errorMessage}'");
        }

        var loginResponse = await response.Content.ReadFromJsonAsync<LoginResponse>()
                    ?? throw new InvalidOperationException("No result payload found");
        restApiContext.BearerToken = loginResponse.Token;
    }
}
