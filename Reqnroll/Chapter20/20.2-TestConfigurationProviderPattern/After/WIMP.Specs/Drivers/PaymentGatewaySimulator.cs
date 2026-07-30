using WIMP.App.Models;
using WIMP.App.Services;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class PaymentGatewaySimulator(TestConfigurationProvider testConfigurationProvider)
{
    public SimulatedPaymentGateway Start()
    {
        string url = testConfigurationProvider.PaymentGateway.Url;
        string apiKey = testConfigurationProvider.PaymentGateway.ApiKey;

        return new SimulatedPaymentGateway(url, apiKey);
    }
}

/// <summary>
/// This class demonstrates the behavior of an externally provided stub service.
/// </summary>
public class SimulatedPaymentGateway : IPaymentGateway
{
    private readonly List<Payment> payments = new();
    private bool isDisposed;
    private static bool isRunning;

    public SimulatedPaymentGateway(string url, string apiKey)
    {
        if (isRunning)
        {
            throw new InvalidOperationException("The payment gateway simulator has been started already");
        }

        Console.WriteLine($"Starting Payment Gateway simulator at {url} with API key {apiKey}");
        Thread.Sleep(500); // simulating slow start
        isRunning = true;
    }

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
}
