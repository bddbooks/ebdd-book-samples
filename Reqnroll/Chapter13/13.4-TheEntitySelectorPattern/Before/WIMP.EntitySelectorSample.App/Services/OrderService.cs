using WIMP.EntitySelectorSample.App.Infrastructure;
using WIMP.EntitySelectorSample.App.Models;

namespace WIMP.EntitySelectorSample.App.Services;

public static class OrderService
{
    public static Order PlaceOrder(string? customerName, string pizzaName, TimeSpan placingTime, int? forcedOrderNo = null)
    {
        if (customerName == null || !AuthenticationService.IsLoggedIn(customerName))
        {
            throw new InvalidOperationException($"Customer '{customerName}' is not logged in.");
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
