using WIMP.App.Data;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class NotificationService(IDataRepository repository)
{
    public void SendCancellationNotification(string customerName)
    {
        repository.InsertNotification(new Notification
        {
            CustomerName = customerName,
            Message = "Your order has been cancelled."
        });
    }

    public void SendDelayNotification(string customerName)
    {
        repository.InsertNotification(new Notification
        {
            CustomerName = customerName,
            Message = "Your order has been delayed."
        });
    }

    public IEnumerable<Notification> GetNotifications(string customerName) =>
        repository.GetNotificationsByCustomerName(customerName);
}
