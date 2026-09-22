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

public class StubDataRepository(StubDataStore dataStore) : IDataRepository
{
    public Customer? GetCustomerByName(string customerName)
    {
        return dataStore.Customers.GetValueOrDefault(customerName);
    }

    public void InsertCustomer(Customer customer)
    {
        dataStore.Customers.Add(customer.Name, customer);
    }

    public void InsertSession(Session session)
    {
        dataStore.Sessions.Add(session.Token, session);
    }

    public void DeleteSession(string token)
    {
        dataStore.Sessions.Remove(token);
    }

    public Session? GetSessionByToken(string token)
    {
        return dataStore.Sessions.GetValueOrDefault(token);
    }

    public void InsertCoupon(Coupon coupon)
    {
        dataStore.Coupons.Add(coupon);
    }

    public IReadOnlyCollection<Coupon> GetCouponsByEmail(string customerEmail)
    {
        return dataStore.Coupons
            .Where(c => string.Equals(c.CustomerEmail, customerEmail, StringComparison.OrdinalIgnoreCase))
            .ToArray();
    }

    public void InsertNotification(Notification notification)
    {
        dataStore.Notifications.Add(notification);
    }

    public IReadOnlyCollection<Notification> GetNotificationsByCustomerName(string customerName)
    {
        return dataStore.Notifications
            .Where(c => string.Equals(c.CustomerName, customerName, StringComparison.OrdinalIgnoreCase))
            .ToArray();
    }

    public void InsertPromotion(Promotion promotion)
    {
        dataStore.Promotions.Add(promotion);
    }

    public Promotion? GetPromotionByName(string name)
    {
        return dataStore.Promotions
            .FirstOrDefault(c => string.Equals(c.Name, name, StringComparison.OrdinalIgnoreCase));
    }

    public Promotion UpdatePromotion(Promotion promotion, bool? isActive = null)
    {
        if (isActive is not null)
        {
            promotion.IsActive = isActive.Value;
        }
        return promotion;
    }

    public Order InsertOrder(Order order)
    {
        order.OrderNo = dataStore.GetNextOrderNo();
        dataStore.Orders.Add(order.OrderNo, order);
        return order;
    }

    public Order? GetOrderByOrderNo(int orderNo)
    {
        return dataStore.Orders.GetValueOrDefault(orderNo);
    }

    public Order UpdateOrder(Order order, OrderStatus? status = null, string? deliveryAddress = null,
        DeliveryMethod? deliveryMethod = null, OrderCollectionDetails? collectionDetails = null)
    {
        if (status is not null)
        {
            order.Status = status.Value;
        }

        if (deliveryAddress is not null)
        {
            order.DeliveryAddress = deliveryAddress;
        }

        if (deliveryMethod is not null)
        {
            order.DeliveryMethod = deliveryMethod.Value;
        }

        if (collectionDetails is not null)
        {
            order.OrderCollectionDetails = collectionDetails;
        }

        return order;
    }

    public IReadOnlyCollection<Order> GetOrdersByStatus(OrderStatus orderStatus)
    {
        return dataStore.Orders.Values
            .Where(o => o.Status == orderStatus)
            .ToArray();
    }

    public IReadOnlyCollection<DailyPizzaSales> GetDailyPizzaSalesBetweenDates(DateOnly startDay, DateOnly endDay)
    {
        return dataStore.DailyPizzaSalesEntries
            .Where(e => e.Date >= startDay && e.Date <= endDay)
            .ToArray();
    }

    public void InsertDailyPizzaSalesEntries(IEnumerable<DailyPizzaSales> salesEntries)
    {
        dataStore.DailyPizzaSalesEntries.AddRange(salesEntries);
    }
    public void InsertMenuItem(MenuItem menuItem)
    {
        dataStore.MenuItems.Add(menuItem);
    }

    public void SetMenuItems(IEnumerable<MenuItem> menuItems)
    {
        dataStore.MenuItems.Clear();
        dataStore.MenuItems.AddRange(menuItems);
    }

    public IReadOnlyCollection<MenuItem> GetMenuItems()
    {
        return dataStore.MenuItems.ToArray();
    }
}
