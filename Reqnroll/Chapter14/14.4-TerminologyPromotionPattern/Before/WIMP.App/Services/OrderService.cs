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

public class OrderService(DataContext dataContext, PromotionService promotionService, EmailService emailService)
{
    public Order PlaceOrder(Order order)
    {
        string customerName = AuthenticationService.GetAuthenticatedCustomerName() ??
            throw new InvalidOperationException("Customer is not authenticated.");

        order.OrderNo = dataContext.GetNextOrderNo();
        order.CustomerName = customerName;
        order.PlacingTime = DateTimeOffset.Now;

        bool hasTooManyLargePizzas = order.Items.Count(i => i.Size == PizzaSize.Large) > 4;
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
