/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

namespace WIMP.App.Infrastructure;

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

    public string? AuthenticatedCustomerName { get; set; }
}
