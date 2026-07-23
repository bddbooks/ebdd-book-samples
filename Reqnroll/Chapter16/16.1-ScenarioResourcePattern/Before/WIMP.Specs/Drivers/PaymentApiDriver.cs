using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class PaymentApiDriver(RestApiContext restApiContext)
{
    public TestAction<Payment> AuthorizePaymentFor(int orderNo) =>
        new LambdaAction<Payment>("Authorize payment", async () =>
            await restApiContext.ProcessRequest<Payment>(
                "Authorize payment", HttpMethod.Post, $"/api/payment/authorize?orderNo={orderNo}"));
}
