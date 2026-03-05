using WIMP.IntroduceAndReferSample.App.Infrastructure;
using WIMP.IntroduceAndReferSample.App.Models;

namespace WIMP.IntroduceAndReferSample.App.Services;

public static class OrderService
{
    public static Order PlaceOrder(string? customerName, string pizzaName, int? forcedOrderNo = null)
    {
        if (customerName == null || !AuthenticationService.IsLoggedIn(customerName))
        {
            throw new InvalidOperationException($"Customer '{customerName}' is not logged in.");
        }

        int orderNo = forcedOrderNo ?? DataContext.Instance.GetNextOrderNo();
        var order = new Order(orderNo, customerName, pizzaName);
        SetStatus(order, OrderStatus.Placed);
        DataContext.Instance.SaveOrder(order);
        return order;
    }

    private static void SetStatus(Order order, OrderStatus status)
    {
        order.Status = status;
    }

    public static void CancelOrder(string? customerName, int orderNo)
    {
        var order = DataContext.Instance.GetOrderByOrderNr(orderNo) ??
            throw new InvalidOperationException($"Order {orderNo} does not exist.");

        if (order.CustomerName != customerName)
        {
            throw new InvalidOperationException($"Order {orderNo} belongs to {order.CustomerName}, not {customerName}.");
        }

        NotificationService.SendCancellationNotification(customerName);
        order.Status = OrderStatus.Cancelled;
    }

    public static Order? GetOrder(int orderNo)
    {
        return DataContext.Instance.GetOrderByOrderNr(orderNo);
    }
}
