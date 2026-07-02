using WIMP.App.Models;

namespace WIMP.Specs.Support;

public class OrderObjectMother
{
    private readonly Order order = new();

    public OrderObjectMother()
    {
        // add default item
        order.AddItem(DomainDefaults.PizzaItemDefaultInstance());
        // add default address
        order.DeliveryAddress = DomainDefaults.DeliveryAddress;
    }

    public Order Build()
    {
        return order;
    }

    public OrderObjectMother WithAdditionalItem(
        string? name = null, string? size = null, PizzaStyle? style = null)
    {
        var pizzaItem = DomainDefaults.PizzaItemDefaultInstance(name, size, style);
        order.AddItem(pizzaItem);
        return this;
    }

    public OrderObjectMother WithItem(
        string? name = null, string? size = null, PizzaStyle? style = null)
    {
        order.ClearItems(); // remove existing items
        return WithAdditionalItem(name, size, style);
    }

    public OrderObjectMother WithItems(int quantity,
        string? name = null, string? size = null, PizzaStyle? style = null)
    {
        order.ClearItems(); // remove existing items
        for (int i = 0; i < quantity; i++)
        {
            WithAdditionalItem(name, size, style);
        }
        return this;
    }
}
