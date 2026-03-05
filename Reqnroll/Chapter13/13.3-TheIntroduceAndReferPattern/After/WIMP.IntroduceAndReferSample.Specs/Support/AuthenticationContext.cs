namespace WIMP.IntroduceAndReferSample.Specs.Support;

/// <summary>
/// Context class for sharing authentication-related data between step definition classes.
/// This pattern allows step definitions in different classes to access the same data
/// through dependency injection without relying on static fields.
/// </summary>
public class AuthenticationContext
{
    public string? LoggedInCustomerName { get; set; }
}
