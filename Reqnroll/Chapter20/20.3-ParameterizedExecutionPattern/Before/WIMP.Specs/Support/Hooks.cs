using Reqnroll;

using WIMP.App.Data;
using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

[Binding]
public class Hooks(AppHostingContext appHostingContext, IDataRepository dataRepository, PaymentGatewaySimulator paymentGatewaySimulator, IDatabaseDriver databaseDriver)
{
    [BeforeScenario(Order = 0)]
    public async Task ResetDatabase()
    {
        await databaseDriver.RecreateDatabase();
    }

    [BeforeScenario("@payment_gateway", Order = 1)]
    public void InitializePaymentGateway()
    {
        appHostingContext.PaymentGateway = paymentGatewaySimulator.Start();
    }

    [BeforeScenario(Order = 2)]
    public void CreateAppHost()
    {
        appHostingContext.AppHost = new WimpAppHost(dataRepository, appHostingContext.PaymentGateway);
    }

    [AfterScenario]
    public void DisposeAppHost()
    {
        appHostingContext.AppHost?.Dispose();
    }

    [AfterScenario("@payment_gateway")]
    public void DisposePaymentGateway()
    {
        appHostingContext.PaymentGateway?.Stop();
    }
}
