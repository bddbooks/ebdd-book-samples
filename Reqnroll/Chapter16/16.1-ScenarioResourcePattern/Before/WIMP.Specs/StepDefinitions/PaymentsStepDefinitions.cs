using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class PaymentsStepDefinitions(OrderingContext orderingContext, AppHostingContext appHostingContext, PaymentApiDriver paymentApiDriver)
{
    private Payment? payment;

    [When("their payment is authorised by the payment gateway")]
    public async Task WhenTheirPaymentIsAuthorisedByThePaymentGateway()
    {
        int orderNo = orderingContext.PlacedOrderNo ?? throw new InvalidOperationException("No order placed.");
        payment = await paymentApiDriver.AuthorizePaymentFor(orderNo).Execute();
    }

    [Then("they should be shown a success message with the payment reference")]
    public void ThenTheyShouldBeShownASuccessMessageWithThePaymentReference()
    {
        Assert.IsNotNull(payment, "No payment was authorized");
        Assert.IsTrue(payment.Success, "The payment should be successful.");
        Assert.IsNotEmpty(payment.PaymentReference, "The response should contain a payment reference");
        Assert.IsNotNull(appHostingContext.PaymentGateway?.GetPaymentByReference(payment.PaymentReference), "Payment reference is invalid");
    }
}
