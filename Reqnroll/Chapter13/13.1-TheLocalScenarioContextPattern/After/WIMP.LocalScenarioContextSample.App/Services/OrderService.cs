using WIMP.LocalScenarioContextSample.App.Infrastructure;
using WIMP.LocalScenarioContextSample.App.Models;

namespace WIMP.LocalScenarioContextSample.App.Services;

public static class OrderService
{
    public static void PlaceOrder(string? customerName, int orderNo, string pizzaName)
    {
        if (customerName == null || !AuthenticationService.IsLoggedIn(customerName))
        {
            throw new InvalidOperationException($"Customer '{customerName}' is not logged in.");
        }

        var order = new Order(orderNo, customerName, pizzaName);
        SetStatus(order, OrderStatus.Placed);
        DataContext.Instance.SaveOrder(order);
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
}
