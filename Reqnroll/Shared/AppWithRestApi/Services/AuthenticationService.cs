using System.Net.Mail;

using WIMP.App.Data;
using WIMP.App.Models;

namespace WIMP.App.Services;

/// <summary>
/// Manages customer registration and authentication sessions using simple Bearer tokens.
/// </summary>
public class AuthenticationService(IDataRepository repository)
{
    /// <summary>The single accepted password for all users in this simulated environment.</summary>
    private const string AcceptedPassword = "Pa22w0rd!";

    public ServiceResult<Customer> Register(string customerName, string email)
    {
        if (string.IsNullOrWhiteSpace(customerName))
        {
            return ServiceResult<Customer>.Failure("Customer name is required.");
        }

        if (!IsValidEmail(email))
        {
            return ServiceResult<Customer>.Failure("A valid email address is required.");
        }

        if (repository.GetCustomerByName(customerName) != null)
        {
            return ServiceResult<Customer>.Failure($"A customer named '{customerName}' is already registered.");
        }

        var customer = new Customer { Name = customerName, Email = email };
        repository.InsertCustomer(customer);
        return ServiceResult<Customer>.Success(customer);
    }

    public ServiceResult<string> Login(string customerName, string password)
    {
        if (string.IsNullOrWhiteSpace(customerName))
        {
            return ServiceResult<string>.Failure("Customer name is required.");
        }

        if (password != AcceptedPassword)
        {
            return ServiceResult<string>.Failure("Invalid password.");
        }

        string token = Guid.NewGuid().ToString("N");
        repository.InsertSession(new Session(token, customerName));
        return ServiceResult<string>.Success(token);
    }

    public void Logout(string token) => repository.DeleteSession(token);

    public string? GetCustomerName(string? token)
    {
        return token is null ? null : repository.GetSessionByToken(token)?.CustomerName;
    }

    public string? ExtractToken(string? authorizationHeader)
    {
        if (authorizationHeader is null)
        {
            return null;
        }

        const string bearer = "Bearer ";
        return authorizationHeader.StartsWith(bearer, StringComparison.OrdinalIgnoreCase)
            ? authorizationHeader[bearer.Length..]
            : null;
    }

    private static bool IsValidEmail(string email)
    {
        if (string.IsNullOrWhiteSpace(email))
        {
            return false;
        }

        try
        {
            _ = new MailAddress(email);
            return true;
        }
        catch (FormatException)
        {
            return false;
        }
    }
}
