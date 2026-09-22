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

public interface IDataRepository
{
    public Customer? GetCustomerByName(string customerName);
    public void InsertCustomer(Customer customer);

    public void InsertSession(Session session);
    public void DeleteSession(string token);
    public Session? GetSessionByToken(string token);
    public void InsertCoupon(Coupon coupon);
    public IReadOnlyCollection<Coupon> GetCouponsByEmail(string customerEmail);
    public void InsertNotification(Notification notification);
    public IReadOnlyCollection<Notification> GetNotificationsByCustomerName(string customerName);
    public void InsertPromotion(Promotion promotion);
    public Promotion? GetPromotionByName(string name);
    public Promotion UpdatePromotion(Promotion promotion, bool? isActive = null);
    public Order InsertOrder(Order order);
    public Order? GetOrderByOrderNo(int orderNo);
    public Order UpdateOrder(Order order, OrderStatus? status = null, string? deliveryAddress = null, DeliveryMethod? deliveryMethod = null, OrderCollectionDetails? collectionDetails = null);
    public IReadOnlyCollection<Order> GetOrdersByStatus(OrderStatus orderStatus);
    public void InsertDailyPizzaSalesEntries(IEnumerable<DailyPizzaSales> salesEntries);
    public IReadOnlyCollection<DailyPizzaSales> GetDailyPizzaSalesBetweenDates(DateOnly startDay, DateOnly endDay);
    public void InsertMenuItem(MenuItem menuItem);
    public void SetMenuItems(IEnumerable<MenuItem> menuItems);
    public IReadOnlyCollection<MenuItem> GetMenuItems();
}
