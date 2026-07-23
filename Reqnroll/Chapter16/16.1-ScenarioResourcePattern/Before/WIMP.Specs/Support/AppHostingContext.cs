using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

public class AppHostingContext
{
    public WimpAppHost AppHost { get; set; } = null!;
    public PaymentGatewaySimulator? PaymentGateway { get; set; }
}
