using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

public class AppHostingContext
{
    public WimpAppHost? AppHost { get; set; }
    public SimulatedPaymentGateway? PaymentGateway { get; set; }
}
