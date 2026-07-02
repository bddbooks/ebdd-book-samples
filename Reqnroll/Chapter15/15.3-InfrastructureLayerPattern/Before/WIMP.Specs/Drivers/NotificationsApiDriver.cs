using System.Net.Http.Json;

using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class NotificationsApiDriver(AppHostingContext appHostingContext)
{
    public async Task<IReadOnlyCollection<Notification>> GetNotifications(string customerName)
    {
        return await appHostingContext.AppHost.CreateClient()
            .GetFromJsonAsync<Notification[]>(
                $"/api/notifications/{customerName}")
               ?? throw new InvalidOperationException("No result payload found");
    }
}
