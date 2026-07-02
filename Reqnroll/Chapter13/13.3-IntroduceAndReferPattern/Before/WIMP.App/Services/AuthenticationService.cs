using WIMP.App.Infrastructure;

namespace WIMP.App.Services;

public static class AuthenticationService
{
    public static void Login(string customerName)
    {
        UserSession.Current.LoggedInCustomerName = customerName;
    }

    public static bool IsLoggedIn(string? customerName)
    {
        return customerName != null && UserSession.Current.LoggedInCustomerName == customerName;
    }
}
