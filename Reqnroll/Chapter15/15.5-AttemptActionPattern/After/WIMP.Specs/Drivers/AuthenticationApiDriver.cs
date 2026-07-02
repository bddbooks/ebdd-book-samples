using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class AuthenticationApiDriver(RestApiContext restApiContext)
{
    public TestAction<LoginResponse> Login(string customerName, string password) =>
        new LambdaAction<LoginResponse>("Login", async () =>
        {
            var loginResponse = await restApiContext.ProcessRequest<LoginResponse>(
                "Login", HttpMethod.Post, "/api/auth/login",
                new LoginRequest(customerName, password));
            restApiContext.BearerToken = loginResponse.Token;
            return loginResponse;
        });
}
