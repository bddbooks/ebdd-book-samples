using System.Diagnostics;
using System.Net;

using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class AuthenticationApiDriver(RestApiContext restApiContext)
{
    public async Task<LoginResponse> PerformLogin(string customerName, string password)
    {
        Console.WriteLine("Executing Login...");
        var stopwatch = Stopwatch.StartNew();
        try
        {
            var loginResponse = await restApiContext.ProcessRequest<LoginResponse>(
                "Login", HttpMethod.Post, "/api/auth/login",
                new LoginRequest(customerName, password));
            Console.WriteLine(
                $"Login executed successfully in {stopwatch.Elapsed}.");

            restApiContext.BearerToken = loginResponse.Token;
            return loginResponse;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Login failed: {ex.Message}");
            throw;
        }
    }

    public async Task PerformRegistration(string customerName, string email)
    {
        Console.WriteLine("Executing Register...");
        var stopwatch = Stopwatch.StartNew();
        try
        {
            await restApiContext.ProcessRequest<VoidReturn>(
                "Register", HttpMethod.Post, "/api/auth/register",
                new RegisterRequest(customerName, email), HttpStatusCode.Created);
            Console.WriteLine(
                $"Register executed successfully in {stopwatch.Elapsed}.");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Register failed: {ex.Message}");
            throw;
        }
    }

}
