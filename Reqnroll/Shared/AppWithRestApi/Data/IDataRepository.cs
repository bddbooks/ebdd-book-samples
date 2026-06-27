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
    public Order UpdateOrder(Order order, OrderStatus? status = null, string? deliveryAddress = null);
    public IReadOnlyCollection<Order> GetOrdersByStatus(OrderStatus orderStatus);
}
