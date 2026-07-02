using WIMP.App.Infrastructure;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class OrderService(DataContext dataContext, PromotionService promotionService, EmailService emailService)
{
    public Order PlaceOrder(Order order)
    {
        string customerName = AuthenticationService.GetLoggedInCustomerName() ??
            throw new InvalidOperationException("Customer is not logged in.");

        order.OrderNo = dataContext.GetNextOrderNo();
        order.CustomerName = customerName;
        order.PlacingTime = DateTimeOffset.Now;

        bool hasTooManyLargePizzas = order.Items.Count(i => i.Size == "Large") > 4;
        SetStatus(order, hasTooManyLargePizzas ? OrderStatus.Rejected : OrderStatus.Placed);
        dataContext.SaveOrder(order);
        return order;
    }

    private void SetStatus(Order order, OrderStatus status)
    {
        order.Status = status;
    }

    public Order? GetOrder(int orderNo)
    {
        return dataContext.GetOrderByOrderNr(orderNo);
    }

    public Order? StartWorkOnNextOrder()
    {
        var nextOrder = dataContext.GetPlacedOrders()
            .OrderBy(o => o.PlacingTime)
            .FirstOrDefault();

        if (nextOrder is null)
        {
            return null;
        }

        SetStatus(nextOrder, OrderStatus.InPreparation);
        return nextOrder;
    }

    public void DeliverOrder(int orderNo)
    {
        var order = dataContext.GetOrderByOrderNr(orderNo) ?? throw new InvalidOperationException("Order not found");

        if (order.Status == OrderStatus.Rejected)
        {
            throw new InvalidOperationException("Order is rejected");
        }

        SetStatus(order, OrderStatus.Completed);

        // Check if order contains Margherita pizza and Margherita Friday promotion is active
        if (order.Items.Any(i => i.Name == "Margherita") &&
            promotionService.IsPromotionActive("Margherita Friday"))
        {
            emailService.SendCouponEmail(order.CustomerName, "MARGHERITA25");
        }
    }

    public void SetWaitingForPickup(int orderNo)
    {
        var order = dataContext.GetOrderByOrderNr(orderNo) ?? throw new InvalidOperationException("Order not found");
        SetStatus(order, OrderStatus.WaitingForPickup);
    }

    public void ChangeDeliveryAddress(int orderNo, string newAddress)
    {
        var order = dataContext.GetOrderByOrderNr(orderNo) ?? throw new InvalidOperationException("Order not found");

        if (!CanChangeAddress(order))
        {
            throw new InvalidOperationException("Cannot change delivery address after pickup.");
        }

        order.DeliveryAddress = newAddress;
    }

    private bool CanChangeAddress(Order order)
    {
        // Simulated logic: address can be changed if order hasn't been picked up
        return order.Status <= OrderStatus.WaitingForPickup;
    }
}
