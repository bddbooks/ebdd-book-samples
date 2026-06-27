using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class AuthenticationApiDriver(RestApiContext restApiContext)
{
    public async Task PerformLogin(string customerName, string password)
    {
        var loginResponse = await restApiContext.ProcessRequest<LoginResponse>(
            "Login",
            HttpMethod.Post,
            "/api/auth/login",
            new LoginRequest(customerName, password));
        restApiContext.BearerToken = loginResponse.Token;
    }
}
