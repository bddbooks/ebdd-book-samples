using WIMP.App.Models;
using WIMP.App.RestApi;

namespace WIMP.Specs.Support;

public class PlaceOrderRequestObjectMother
{
    private readonly List<PizzaItem> items = [];
    private readonly string email = DomainDefaults.CustomerEmail;
    private readonly string deliveryAddress = DomainDefaults.CustomerAddress;

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
            DisableMenuIntegrityCheck: false); // in this sample we would like to check if the ordered pizzas are on the menu
    }

    public PlaceOrderRequestObjectMother WithItem(
        string? name = null, PizzaSize? size = null, PizzaStyle? style = null)
    {
        items.Clear();
        items.Add(DomainDefaults.PizzaItemDefaultInstance(name, size, style));
        return this;
    }
}
