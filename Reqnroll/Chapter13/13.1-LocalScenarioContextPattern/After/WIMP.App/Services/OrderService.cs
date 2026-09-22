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

using WIMP.App.Infrastructure;
using WIMP.App.Models;

namespace WIMP.App.Services;

public static class OrderService
{
    public static void PlaceOrder(string? customerName, int orderNo, string pizzaName)
    {
        if (customerName == null || !AuthenticationService.IsAuthenticated(customerName))
        {
            throw new InvalidOperationException($"Customer '{customerName}' is not authenticated.");
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
