/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

using WIMP.App.Models;

namespace WIMP.App.Infrastructure;

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
