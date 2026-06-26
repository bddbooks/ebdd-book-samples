using WIMP.App.Models;

namespace WIMP.App.Infrastructure;

/// <summary>
/// A simple simulation of a database context, using in-memory collections. In a real application,
/// this would likely be replaced with an actual database context (e.g., Entity Framework DbContext) or a repository pattern.
/// </summary>
public class DataContext
{
    private int nextOrderNo = 1;
    private readonly List<Order> orders = new();

    private void InsertOrUpdate<TEntity>(List<TEntity> list, TEntity entity, Predicate<TEntity> match)
    {
        int foundIndex = list.FindIndex(match);

        if (foundIndex < 0)
        {
            list.Add(entity);
        }
        else
        {
            list[foundIndex] = entity;
        }
    }

    public int GetNextOrderNo()
    {
        return nextOrderNo++;
    }

    public Order? GetOrderByOrderNr(int orderNr) =>
        orders.FirstOrDefault(o => o.OrderNo == orderNr);

    public IEnumerable<Order> GetPlacedOrders() =>
        orders.Where(o => o.Status == OrderStatus.Placed);

    public void SaveOrder(Order order)
    {
        InsertOrUpdate(orders, order, o => o.OrderNo == order.OrderNo);
    }
}
