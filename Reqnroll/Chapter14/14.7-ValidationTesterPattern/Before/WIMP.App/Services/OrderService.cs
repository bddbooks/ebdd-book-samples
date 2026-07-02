using System.Net.Mail;

using WIMP.App.Infrastructure;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class OrderService(DataContext dataContext)
{
    public Order PlaceOrder(Order order)
    {
        string customerName = AuthenticationService.GetLoggedInCustomerName() ??
            throw new InvalidOperationException("Customer is not logged in.");

        order.OrderNo = dataContext.GetNextOrderNo();
        order.CustomerName = customerName;
        order.PlacingTime = DateTimeOffset.Now;

        SetStatus(order, OrderStatus.Placed);
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

    public void ProvideCustomerDetails(Order order, ContactDetails contactDetails)
    {
        ValidateContactDetails(contactDetails);
        if (order.Collection == OrderCollection.CustomerCollection)
        {
            ValidateCustomerCollectionContactDetails(contactDetails);
        }

        order.ContactDetails = contactDetails;
    }

    private void ValidateCustomerCollectionContactDetails(ContactDetails contactDetails)
    {
        if (string.IsNullOrEmpty(contactDetails.Email) && string.IsNullOrEmpty(contactDetails.Phone))
        {
            throw new InvalidOperationException("For customer collection email or phone must be specified");
        }
    }

    private void ValidateContactDetails(ContactDetails contactDetails)
    {
        if (string.IsNullOrWhiteSpace(contactDetails.Name))
        {
            throw new InvalidOperationException("Name not specified");
        }

        if (!string.IsNullOrWhiteSpace(contactDetails.Email) && !IsValidEmail(contactDetails.Email))
        {
            throw new InvalidOperationException("Wrong email format");
        }

        if (!string.IsNullOrWhiteSpace(contactDetails.Phone) && !IsValidPhone(contactDetails.Phone))
        {
            throw new InvalidOperationException("Wrong phone number format");
        }

        if (contactDetails.Country == "US" && (string.IsNullOrWhiteSpace(contactDetails.State) || contactDetails.State == "-"))
        {
            throw new InvalidOperationException("For US country the state must be specified");
        }
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
}
