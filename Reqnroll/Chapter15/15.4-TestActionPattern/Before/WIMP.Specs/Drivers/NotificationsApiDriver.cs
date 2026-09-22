using System.Diagnostics;

using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class NotificationsApiDriver(RestApiContext restApiContext)
{
    public async Task<IReadOnlyCollection<Notification>> GetNotifications(string customerName)
    {
        Console.WriteLine("Executing Get notifications...");
        var stopwatch = Stopwatch.StartNew();
        try
        {
            var response = await restApiContext.GetRequest<Notification[]>(
                $"/api/notifications/{customerName}");
            Console.WriteLine(
                $"Get notifications executed successfully in {stopwatch.Elapsed}.");
            return response;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Get notifications failed: {ex.Message}");
            throw;
        }
    }
}
