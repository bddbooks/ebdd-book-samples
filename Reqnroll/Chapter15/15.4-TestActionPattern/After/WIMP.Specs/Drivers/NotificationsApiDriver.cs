using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class NotificationsApiDriver(RestApiContext restApiContext)
{
    public TestAction<IReadOnlyCollection<Notification>> GetNotifications(string customerName) =>
        new LambdaAction<IReadOnlyCollection<Notification>>("Get notifications", async () =>
            await restApiContext.GetRequest<Notification[]>(
                $"/api/notifications/{customerName}"));
}
