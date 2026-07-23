using Reqnroll;

using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

[Binding]
public class Hooks(AppHostingContext appHostingContext)
{
    [BeforeScenario]
    public void CreateAppHost()
    {
        appHostingContext.PaymentGateway = PaymentGatewaySimulator.Start();
        appHostingContext.AppHost = new WimpAppHost(appHostingContext.PaymentGateway);
    }

    [AfterScenario]
    public void DisposeAppHost()
    {
        appHostingContext.AppHost.Dispose();
    }

    [AfterScenario]
    public void DisposePaymentGateway()
    {
        appHostingContext.PaymentGateway?.Stop();
    }
}
