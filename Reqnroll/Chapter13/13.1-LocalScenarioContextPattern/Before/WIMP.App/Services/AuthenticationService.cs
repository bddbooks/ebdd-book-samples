using WIMP.App.Infrastructure;

namespace WIMP.App.Services;

public static class AuthenticationService
{
    public static void Login(string customerName)
    {
        UserSession.Current.AuthenticatedCustomerName = customerName;
    }

    public static bool IsAuthenticated(string? customerName)
    {
        return customerName != null && UserSession.Current.AuthenticatedCustomerName == customerName;
    }
}
