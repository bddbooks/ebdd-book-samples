using WIMP.App.Models;
using WIMP.App.RestApi;

namespace WIMP.Specs.Support;

public class PlaceOrderRequestObjectMother
{
    private readonly List<PizzaItem> items = [];
    private readonly string email = DomainDefaults.CustomerEmail;
    private readonly string deliveryAddress = DomainDefaults.CustomerAddress;
    private DateTimeOffset? expectedDeliveryTime;

    public PlaceOrderRequestObjectMother()
    {
        items.Add(DomainDefaults.PizzaItemDefaultInstance());
    }

    public PlaceOrderRequest Build()
    {
        return new(
            items.ToArray(),
            deliveryAddress,
            email,
            ExpectedDeliveryTime: expectedDeliveryTime);
    }

    public PlaceOrderRequestObjectMother WithExpectedDeliveryTime(DateTimeOffset time)
    {
        expectedDeliveryTime = time;
        return this;
    }
}
