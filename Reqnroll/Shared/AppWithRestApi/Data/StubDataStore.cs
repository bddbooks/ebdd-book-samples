using WIMP.App.Models;

namespace WIMP.App.Data;

/// <summary>
/// In-memory data store simulating a database. Used as a singleton so all requests share the same state.
/// </summary>
public class StubDataStore
{
    private int nextOrderNo = 1000;

    public Dictionary<string, Customer> Customers { get; } = new(StringComparer.OrdinalIgnoreCase);
    public Dictionary<string, Session> Sessions { get; } = new(StringComparer.OrdinalIgnoreCase);
    public Dictionary<int, Order> Orders { get; } = [];
    public List<Notification> Notifications { get; } = [];
    public List<Coupon> Coupons { get; } = [];
    public List<Promotion> Promotions { get; } = [];

    public void Reset()
    {
        Customers.Clear();
        Sessions.Clear();
        Orders.Clear();
        Notifications.Clear();
        Coupons.Clear();
        Promotions.Clear();
        nextOrderNo = 1000;
    }

    public int GetNextOrderNo() => nextOrderNo++;
}
