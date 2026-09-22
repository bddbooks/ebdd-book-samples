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

using System.Net.Mail;

using WIMP.App.Data;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class OrderService(IDataRepository repository, PromotionService promotionService, NotificationService notificationService, EmailService emailService, ITimeService timeService, MenuService menuService, MessageService messageService, ILogger<OrderService> logger)
{
    private const int DeliveryTimeMinutes = 45;
    private const int MaxLargePizzasPerOrder = 4;

    public ServiceResult<Order> PlaceOrder(string customerName, string customerEmail, List<PizzaItem> items, string deliveryAddress, DateTimeOffset? testOnlyPlacingTime = null, DateTimeOffset? testOnlyExpectedDeliveryTime = null, bool testOnlyDisableMenuIntegrityCheck = true)
    {
        int largePizzaCount = items.Count(i => i.Size == PizzaSize.Large);
        var placingTime = timeService.GetCurrentTime();
#if DEBUG
        // Test-only API: for testing the placing time can be overridden
        placingTime = testOnlyPlacingTime ?? placingTime;
#endif
        var expectedDeliveryTime = placingTime.AddMinutes(DeliveryTimeMinutes);
#if DEBUG
        // Test-only API: for testing the expected delivery time can be overridden
        expectedDeliveryTime = testOnlyExpectedDeliveryTime ?? expectedDeliveryTime;
#endif
        if (largePizzaCount > MaxLargePizzasPerOrder)
        {
            logger.LogInformation("Rejecting order because of size limit");
            return ServiceResult<Order>.Failure(messageService.GetMessage("en-US", "cannot-deliver-too-many-large-pizzas", largePizzaCount));
        }

        decimal price;
        if (testOnlyDisableMenuIntegrityCheck)
        {
            price = items.Count * 12; // pricing simulation
        }
        else
        {
            var menu = menuService.LoadMenu();
            price = 0m;
            foreach (var orderItem in items)
            {
                var menuItem = menu.FirstOrDefault(mi => mi.Name == orderItem.Name);
                if (menuItem == null)
                {
                    return ServiceResult<Order>.Failure($"Could not find pizza '{orderItem.Name}' on menu.");
                }
                price += menuItem.Price;
            }
        }

        var order = repository.InsertOrder(new Order
        {
            CustomerName = customerName,
            CustomerEmail = customerEmail,
            Items = items,
            DeliveryAddress = deliveryAddress,
            Status = OrderStatus.Placed,
            PlacingTime = placingTime.TimeOfDay,
            Price = price
        });

        // scheduling to send a delay notification if the order is not delivered within 45 minutes
        SubscribeForDelayNotification(expectedDeliveryTime, order.OrderNo, customerName);

        logger.LogInformation("Order {OrderNo} placed", order.OrderNo);

#if SIMULATED_OREDER_REJECTION
        if (order.PlacingTime.Seconds == 30)
        {
            logger.LogWarning("Order {OrderNo} has been rejected, because of a simulated error", order.OrderNo);
            repository.UpdateOrder(order, status: OrderStatus.Rejected);
        }
#endif

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

    public ServiceResult<Order> SetForCustomerCollection(int orderNo, string customerName)
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

        if (order.DeliveryMethod == DeliveryMethod.CustomerCollection)
        {
            return ServiceResult<Order>.Failure($"Order {orderNo} is already set for customer-collection.");
        }

        if (order.Status is not (OrderStatus.Placed or OrderStatus.InPreparation))
        {
            return ServiceResult<Order>.Failure($"Order {orderNo} cannot be set to customer-collection in its current status.");
        }

        var collectionDetails = new OrderCollectionDetails
        {
            OrderNo = orderNo,
            BoxesToBeCollected = order.Items.Count,
            ContactDetailsConfirmationRequested = true
        };
        order = repository.UpdateOrder(order, deliveryMethod: DeliveryMethod.CustomerCollection, collectionDetails: collectionDetails);
        return ServiceResult<Order>.Success(order);
    }

    public ServiceResult<Order> ProvideContactDetails(int orderNo, ContactDetails contactDetails)
    {
        var order = repository.GetOrderByOrderNo(orderNo);
        if (order is null)
        {
            return ServiceResult<Order>.Failure($"Order {orderNo} not found.");
        }

        bool isValid = ValidateContactDetails(contactDetails, out string validationErrorMessage);
        if (isValid && order.DeliveryMethod == DeliveryMethod.CustomerCollection)
        {
            isValid = ValidateCustomerCollectionContactDetails(contactDetails, out validationErrorMessage);
        }

        if (!isValid)
        {
            return ServiceResult<Order>.Failure(validationErrorMessage);
        }

        order.ContactDetails = contactDetails;
        return ServiceResult<Order>.Success(order);
    }

    private bool ValidateCustomerCollectionContactDetails(ContactDetails contactDetails, out string errorMessage)
    {
        if (string.IsNullOrEmpty(contactDetails.Email) && string.IsNullOrEmpty(contactDetails.Phone))
        {
            errorMessage = "For customer collection email or phone must be specified";
            return false;
        }

        errorMessage = "";
        return true;
    }

    private bool ValidateContactDetails(ContactDetails contactDetails, out string errorMessage)
    {
        if (string.IsNullOrWhiteSpace(contactDetails.Name))
        {
            errorMessage = "Name not specified";
            return false;
        }

        if (!string.IsNullOrWhiteSpace(contactDetails.Email) && !IsValidEmail(contactDetails.Email))
        {
            errorMessage = "Wrong email format";
            return false;
        }

        if (!string.IsNullOrWhiteSpace(contactDetails.Phone) && !IsValidPhone(contactDetails.Phone))
        {
            errorMessage = "Wrong phone number format";
            return false;
        }

        if (contactDetails.Country == "US" && (string.IsNullOrWhiteSpace(contactDetails.State) || contactDetails.State == "none"))
        {
            errorMessage = "For US country the state must be specified";
            return false;
        }

        errorMessage = "";
        return true;
    }

    private bool IsValidPhone(string phone)
    {
        return phone.Length >= 6 && phone.All(char.IsAsciiDigit);
    }

    private static bool IsValidEmail(string email)
    {
        try
        {
            var mailAddress = new MailAddress(email);
            return mailAddress.Address == email;
        }
        catch (FormatException)
        {
            return false;
        }
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
#if SIMULATED_BUG
            .OrderByDescending(o => o.PlacingTime)
#else
            .OrderBy(o => o.PlacingTime)
#endif
            .FirstOrDefault();

        if (next is null)
        {
            return null;
        }

        next = repository.UpdateOrder(next, status: OrderStatus.InPreparation);
        logger.LogInformation("Taken next order: {OrderNo}", next.OrderNo);
        return next;
    }
}
