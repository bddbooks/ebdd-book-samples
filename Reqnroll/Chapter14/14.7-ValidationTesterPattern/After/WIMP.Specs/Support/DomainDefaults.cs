using WIMP.App.Models;

namespace WIMP.Specs.Support;

public static class DomainDefaults
{
    public const string PizzaName = "Margherita";
    public const PizzaSize PizzaSize = App.Models.PizzaSize.Medium;
    public const PizzaStyle PizzaStyle = App.Models.PizzaStyle.Regular;

    public static PizzaItem PizzaItemDefaultInstance(
        string? name = null, PizzaSize? size = null, PizzaStyle? style = null)
    {
        return new PizzaItem(
            name ?? PizzaName,
            size ?? PizzaSize,
            style ?? PizzaStyle);
    }

    public const OrderCollection OrderCollection = App.Models.OrderCollection.Delivery;

    public const string CustomerName = "Rebecca";
    public const string CustomerEmail = "becca@galaxy.uni";
    public const string CustomerPhone = "12334456";
    public const string Country = "France";

    public static ContactDetails ContactDetailsDefaultInstance()
    {
        return new ContactDetails
        {
            Name = CustomerName,
            Email = CustomerEmail,
            Phone = CustomerPhone,
            Country = Country,
            State = null
        };
    }
}
