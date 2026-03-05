using WIMP.IntroduceAndReferSample.App.Infrastructure;
using WIMP.IntroduceAndReferSample.App.Models;

namespace WIMP.IntroduceAndReferSample.App.Services;

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
