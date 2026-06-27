using WIMP.App.Data;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class OrderService(IDataRepository repository, PromotionService promotionService, NotificationService notificationService, EmailService emailService, ITimeService timeService)
{
    private const int DeliveryTimeMinutes = 45;
    private const int MaxLargePizzasPerOrder = 4;

    public ServiceResult<Order> PlaceOrder(string customerName, string customerEmail, List<PizzaItem> items, string deliveryAddress, DateTimeOffset? testOnlyPlacingTime = null, DateTimeOffset? testOnlyExpectedDeliveryTime = null)
    {
        int largePizzaCount = items.Count(i => i.Size == PizzaSize.Large);
        var placingTime = timeService.GetCurrentTime();
#if DEBUG
        // Test-only API: for testing the placing time can be overridden
        placingTime = testOnlyPlacingTime ?? placingTime;
#endif
        var expectedDeliveryTime = placingTime.AddMinutes(45);
#if DEBUG
        // Test-only API: for testing the expected delivery time can be overridden
        expectedDeliveryTime = testOnlyExpectedDeliveryTime ?? expectedDeliveryTime;
#endif
        if (largePizzaCount > MaxLargePizzasPerOrder)
        {
            var rejectedOrder = repository.InsertOrder(new Order
            {
                CustomerName = customerName,
                CustomerEmail = customerEmail,
                Items = items,
                DeliveryAddress = deliveryAddress,
                Status = OrderStatus.Rejected,
                StatusMessage = "Cannot deliver more than 4 large pizzas in a single order",
                PlacingTime = placingTime.TimeOfDay
            });
            return ServiceResult<Order>.Success(rejectedOrder);
        }

        var order = repository.InsertOrder(new Order
        {
            CustomerName = customerName,
            CustomerEmail = customerEmail,
            Items = items,
            DeliveryAddress = deliveryAddress,
            Status = OrderStatus.Placed,
            PlacingTime = placingTime.TimeOfDay
        });

        // scheduling to send a delay notification if the order is not delivered within 45 minutes
        SubscribeForDelayNotification(expectedDeliveryTime, order.OrderNo, customerName);

        return ServiceResult<Order>.Success(order);
    }

    private void SubscribeForDelayNotification(DateTimeOffset expectedDeliveryTime, int orderNo, string customerName)
    {
        timeService.SubscribeToTimeChange(time =>
        {
            if (time >= expectedDeliveryTime)
            {
                var order = repository.GetOrderByOrderNo(orderNo);
                if (order is { Status: < OrderStatus.Completed })
                {
                    notificationService.SendDelayNotification(customerName);
                }
                return true;
            }
            return false;
        });
    }

    public Order? GetOrder(int orderNo)
    {
        return repository.GetOrderByOrderNo(orderNo);
    }

    public ServiceResult<Order> CancelOrder(int orderNo, string customerName)
    {
        var order = repository.GetOrderByOrderNo(orderNo);
        if (order is null)
        {
            return ServiceResult<Order>.Failure($"Order {orderNo} not found.");
        }

        if (!string.Equals(order.CustomerName, customerName, StringComparison.OrdinalIgnoreCase))
        {
            return ServiceResult<Order>.Failure($"Order {orderNo} belongs to a different customer.");
        }

        if (order.Status is OrderStatus.Completed or OrderStatus.Cancelled or OrderStatus.Rejected)
        {
            return ServiceResult<Order>.Failure($"Order {orderNo} cannot be cancelled in its current status.");
        }

        order = repository.UpdateOrder(order, status: OrderStatus.Cancelled);
        notificationService.SendCancellationNotification(customerName);
        return ServiceResult<Order>.Success(order);
    }

    public ServiceResult<Order> DeliverOrder(int orderNo)
    {
        var order = repository.GetOrderByOrderNo(orderNo);
        if (order is null)
        {
            return ServiceResult<Order>.Failure($"Order {orderNo} not found.");
        }

        order = repository.UpdateOrder(order, status: OrderStatus.Completed);

        if (order.Items.Any(i => string.Equals(i.Name, "Margherita", StringComparison.OrdinalIgnoreCase)) &&
            promotionService.IsPromotionActive("Margherita Friday"))
        {
            emailService.SendCoupon(order.CustomerEmail, "MARGHERITA25");
        }

        return ServiceResult<Order>.Success(order);
    }

    public ServiceResult<Order> ChangeDeliveryAddress(int orderNo, string newAddress)
    {
        var order = repository.GetOrderByOrderNo(orderNo);
        if (order is null)
        {
            return ServiceResult<Order>.Failure($"Order {orderNo} not found.");
        }

        if (order.Status > OrderStatus.WaitingForPickup)
        {
            return ServiceResult<Order>.Failure("Delivery address can only be changed before the order is picked up.");
        }

        order = repository.UpdateOrder(order, deliveryAddress: newAddress);
        return ServiceResult<Order>.Success(order);
    }

    public ServiceResult<Order> SetWaitingForPickup(int orderNo)
    {
        var order = repository.GetOrderByOrderNo(orderNo);
        if (order is null)
        {
            return ServiceResult<Order>.Failure($"Order {orderNo} not found.");
        }

        if (order.Status != OrderStatus.InPreparation)
        {
            return ServiceResult<Order>.Failure($"Order {orderNo} must be in preparation before it can wait for pickup.");
        }

        order = repository.UpdateOrder(order, status: OrderStatus.WaitingForPickup);
        return ServiceResult<Order>.Success(order);
    }

    public Order? TakeNextOrder()
    {
        var next = repository.GetOrdersByStatus(OrderStatus.Placed)
            .OrderBy(o => o.PlacingTime)
            .FirstOrDefault();

        if (next is null)
        {
            return null;
        }

        next = repository.UpdateOrder(next, status: OrderStatus.InPreparation);
        return next;
    }
}
