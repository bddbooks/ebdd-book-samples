using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class NotificationsApiDriver(RestApiContext restApiContext)
{
    public async Task<IReadOnlyCollection<Notification>> GetNotifications(string customerName)
    {
        return await restApiContext.GetRequest<Notification[]>(
            $"/api/notifications/{customerName}");
    }
}
