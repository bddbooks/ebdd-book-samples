using Reqnroll;

using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

[Binding]
public class Hooks(AppHostingContext appHostingContext)
{
    [BeforeScenario("@payment_gateway", Order = 1)]
    public void InitializePaymentGateway()
    {
        appHostingContext.PaymentGateway = PaymentGatewaySimulator.Start();
    }

    [BeforeScenario(Order = 2)]
    public void CreateAppHost()
    {
        appHostingContext.AppHost = new WimpAppHost(appHostingContext.PaymentGateway);
    }

    [AfterScenario]
    public void DisposeAppHost()
    {
        appHostingContext.AppHost.Dispose();
    }

    [AfterScenario("@payment_gateway")]
    public void DisposePaymentGateway()
    {
        appHostingContext.PaymentGateway?.Stop();
    }
}
