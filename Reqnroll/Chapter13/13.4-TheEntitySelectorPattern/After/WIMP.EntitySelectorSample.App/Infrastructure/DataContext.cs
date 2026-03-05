using WIMP.EntitySelectorSample.App.Models;

namespace WIMP.EntitySelectorSample.App.Infrastructure;

/// <summary>
/// A simple simulation of a database context, using in-memory collections. In a real application,
/// this would likely be replaced with an actual database context (e.g., Entity Framework DbContext) or a repository pattern.
/// </summary>
public class DataContext
{
    public static readonly DataContext Instance = new();

    private int nextOrderNo = 1;
    private readonly Dictionary<int, Order> orders = new();

    public void Reset()
    {
        orders.Clear();
        nextOrderNo = 1;
    }

    public int GetNextOrderNo()
    {
        return nextOrderNo++;
    }

    public Order? GetOrderByOrderNr(int orderNr) =>
        orders.Values.FirstOrDefault(o => o.OrderNo == orderNr);

    public IEnumerable<Order> GetPlacedOrders() =>
        orders.Values.Where(o => o.Status == OrderStatus.Placed);

    public void SaveOrder(Order order)
    {
        orders.Add(order.OrderNo, order);
    }
}
