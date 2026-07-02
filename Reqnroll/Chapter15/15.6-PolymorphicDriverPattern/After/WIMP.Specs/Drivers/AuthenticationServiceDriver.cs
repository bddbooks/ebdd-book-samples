using WIMP.App.RestApi;
using WIMP.App.Services;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class AuthenticationServiceDriver(AuthenticationService authService) : IAuthenticationDriver
{
    public TestAction<LoginResponse> Login(string customerName, string password) =>
        new LambdaAction<LoginResponse>("Login", () =>
        {
            var result = authService.Login(customerName, password);
            return result.Successful ? new LoginResponse(result.Value, customerName) :
                    throw new WimpActionFailedException(result.ErrorMessage);
        });

    public TestAction<VoidReturn> Register(string customerName, string email) =>
        new LambdaAction("Register", () =>
        {
            var result = authService.Register(customerName, email);
            if (!result.Successful)
            {
                throw new WimpActionFailedException(result.ErrorMessage);
            }
        });
}
