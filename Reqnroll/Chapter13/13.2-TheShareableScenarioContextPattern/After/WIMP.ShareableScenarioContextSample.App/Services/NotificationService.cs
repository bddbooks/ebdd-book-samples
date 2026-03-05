using WIMP.ShareableScenarioContextSample.App.Infrastructure;
using WIMP.ShareableScenarioContextSample.App.Models;

namespace WIMP.ShareableScenarioContextSample.App.Services;

public static class NotificationService
{
    public static void SendCancellationNotification(string customerName)
    {
        DataContext.Instance.SaveNotification(new Notification(customerName, "Your order has been cancelled."));
    }

    public static bool WasNotificationSent(string? customerName)
    {
        return DataContext.Instance.GetNotificationsByCustomerName(customerName ?? "n/a").Any();
    }
}
