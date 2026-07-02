using WIMP.App.Models;
using WIMP.App.RestApi;

namespace WIMP.Specs.Support;

public class PlaceOrderRequestObjectMother
{
    private readonly List<PizzaItem> items = new();
    private readonly string email = DomainDefaults.CustomerEmail;
    private readonly string deliveryAddress = DomainDefaults.CustomerAddress;
    private DateTimeOffset? expectedDeliveryTime = null;

    public PlaceOrderRequestObjectMother()
    {
        // add default item
        items.Add(DomainDefaults.PizzaItemDefaultInstance());
    }

    public PlaceOrderRequest Build()
    {
        return new(items.ToArray(), email, deliveryAddress, ExpectedDeliveryTime: expectedDeliveryTime);
    }

    public PlaceOrderRequestObjectMother WithAdditionalItem(
        string? name = null, PizzaSize? size = null, PizzaStyle? style = null)
    {
        var pizzaItem = DomainDefaults.PizzaItemDefaultInstance(name, size, style);
        items.Add(pizzaItem);
        return this;
    }

    public PlaceOrderRequestObjectMother WithItem(
        string? name = null, PizzaSize? size = null, PizzaStyle? style = null)
    {
        items.Clear(); // remove existing items
        return WithAdditionalItem(name, size, style);
    }

    public PlaceOrderRequestObjectMother WithItems(int quantity,
        string? name = null, PizzaSize? size = null, PizzaStyle? style = null)
    {
        items.Clear(); // remove existing items
        for (int i = 0; i < quantity; i++)
        {
            WithAdditionalItem(name, size, style);
        }
        return this;
    }
    public PlaceOrderRequestObjectMother WithExpectedDeliveryTime(
        DateTimeOffset time)
    {
        expectedDeliveryTime = time;
        return this;
    }
}
