namespace WIMP.LocalScenarioContextSample.App.Infrastructure;

/// <summary>
/// As simple simulation of a user session, using AsyncLocal to store the current session
/// for the current async context. In a real application, this would likely be more complex
/// and involve actual user authentication and session management.
/// </summary>
public class UserSession
{
    private static readonly AsyncLocal<UserSession> current = new();

    public static UserSession Current
    {
        get
        {
            current.Value ??= new UserSession();
            return current.Value;
        }
    }

    public string? LoggedInCustomerName { get; set; }
}
