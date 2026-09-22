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
    public List<DailyPizzaSales> DailyPizzaSalesEntries { get; } = [];
    public List<MenuItem> MenuItems = [];

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
