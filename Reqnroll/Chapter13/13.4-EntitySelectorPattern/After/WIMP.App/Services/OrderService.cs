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
    public static Order PlaceOrder(string? customerName, string pizzaName, TimeSpan placingTime, int? forcedOrderNo = null)
    {
        if (customerName == null || !AuthenticationService.IsAuthenticated(customerName))
        {
            throw new InvalidOperationException($"Customer '{customerName}' is not authenticated.");
        }

        int orderNo = forcedOrderNo ?? DataContext.Instance.GetNextOrderNo();
        var order = new Order(orderNo, customerName, pizzaName, placingTime);
        SetStatus(order, OrderStatus.Placed);
        DataContext.Instance.SaveOrder(order);
        return order;
    }

    private static void SetStatus(Order order, OrderStatus status)
    {
        order.Status = status;
    }

    public static Order? GetOrder(int orderNo)
    {
        return DataContext.Instance.GetOrderByOrderNr(orderNo);
    }

    public static Order? StartWorkOnNextOrder()
    {
        var nextOrder = DataContext.Instance.GetPlacedOrders()
            .OrderBy(o => o.PlacingTime)
            .FirstOrDefault();

        if (nextOrder is null)
        {
            return null;
        }

        SetStatus(nextOrder, OrderStatus.InPreparation);
        return nextOrder;
    }
}
