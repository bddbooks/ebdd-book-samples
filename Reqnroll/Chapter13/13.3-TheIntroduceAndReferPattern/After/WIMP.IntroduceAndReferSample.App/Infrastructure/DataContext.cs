using WIMP.IntroduceAndReferSample.App.Models;

namespace WIMP.IntroduceAndReferSample.App.Infrastructure;

/// <summary>
/// A simple simulation of a database context, using in-memory collections. In a real application,
/// this would likely be replaced with an actual database context (e.g., Entity Framework DbContext) or a repository pattern.
/// </summary>
public class DataContext
{
    public static readonly DataContext Instance = new();

    private int nextOrderNo = 1;
    private readonly Dictionary<int, Order> orders = new();
    private readonly List<Notification> notifications = new();

    public void Reset()
    {
        orders.Clear();
        notifications.Clear();
        nextOrderNo = 1;
    }

    public IEnumerable<Notification> GetNotificationsByCustomerName(string customerName) =>
        notifications.Where(n => n.CustomerName == customerName);

    public void SaveNotification(Notification notification)
    {
        notifications.Add(notification);
    }

    public int GetNextOrderNo()
    {
        return nextOrderNo++;
    }

    public Order? GetOrderByOrderNr(int orderNr) =>
        orders.Values.FirstOrDefault(o => o.OrderNo == orderNr);

    public void SaveOrder(Order order)
    {
        orders.Add(order.OrderNo, order);
    }
}
