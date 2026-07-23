using WIMP.App.Models;
using WIMP.App.Services;

namespace WIMP.Specs.Drivers;

public class PaymentGatewaySimulator : IPaymentGateway
{
    private readonly List<Payment> payments = new();

    public Payment Authorize(string customerEmail, decimal amount)
    {
        AssertNotDisposed();

        Payment payment;
        if (customerEmail == "nofunds@example.com")
        {
            Console.WriteLine("Simulating failed authorization...");
            payment = new Payment
            {
                Amount = amount,
                CustomerEmail = customerEmail,
                PaymentReference = Guid.NewGuid().ToString("N"),
                Success = false,
                Message = "Insufficient funds"
            };
        }
        else
        {
            payment = new Payment
            {
                Amount = amount,
                CustomerEmail = customerEmail,
                PaymentReference = Guid.NewGuid().ToString("N"),
                Success = true,
                Message = "OK"
            };
        }

        payments.Add(payment);
        return payment;
    }

    public Payment? GetPaymentByReference(string paymentReference)
    {
        return payments.FirstOrDefault(p => p.PaymentReference == paymentReference);
    }

    private static bool isRunning;
    private bool isDisposed;

    public static PaymentGatewaySimulator Start()
    {
        if (isRunning)
        {
            throw new InvalidOperationException("The payment gateway simulator has been started already");
        }

        Thread.Sleep(500); // simulating slow start
        isRunning = true;
        return new PaymentGatewaySimulator();
    }

    public void Stop()
    {
        AssertNotDisposed();
        isDisposed = true;
        if (!isRunning)
        {
            throw new InvalidOperationException("The payment gateway simulator was not running");
        }
        isRunning = false;
    }

    private void AssertNotDisposed()
    {
        if (isDisposed)
        {
            throw new InvalidOperationException("The payment gateway simulator has been disposed");
        }
    }

    /// <summary>
    /// For starting the payment gateway simulator, use the <see cref="Start"/> method.
    /// </summary>
    public PaymentGatewaySimulator()
    {
    }
}
