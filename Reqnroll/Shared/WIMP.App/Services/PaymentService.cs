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

using WIMP.App.Data;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class PaymentService(IDataRepository repository, IPaymentGateway paymentGateway)
{
    public ServiceResult<Payment> AuthorizePaymentFor(int orderNo)
    {
        var order = repository.GetOrderByOrderNo(orderNo);
        if (order is null)
        {
            return ServiceResult<Payment>.Failure($"Order {orderNo} not found.");
        }

        try
        {
            var payment = paymentGateway.Authorize(order.CustomerEmail, order.Price);
            return ServiceResult<Payment>.Success(payment);
        }
        catch (Exception ex)
        {
            return ServiceResult<Payment>.Failure(ex.Message);
        }
    }
}

public interface IPaymentGateway
{
    public Payment Authorize(string customerEmail, decimal amount);
}

/// <summary>
/// This is a non-functioning placeholder for a real payment gateway service
/// </summary>
public class RealPaymentGateway : IPaymentGateway
{
    public Payment Authorize(string customerEmail, decimal amount)
    {
        throw new NotSupportedException("The real payment gateway cannot be used for testing");
    }
}
