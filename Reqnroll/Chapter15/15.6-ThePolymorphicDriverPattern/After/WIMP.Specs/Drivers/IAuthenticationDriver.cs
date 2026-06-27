using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public interface IAuthenticationDriver
{
    public TestAction<LoginResponse> Login(string customerName, string password);

    public TestAction<VoidReturn> Register(string customerName, string email);
}
