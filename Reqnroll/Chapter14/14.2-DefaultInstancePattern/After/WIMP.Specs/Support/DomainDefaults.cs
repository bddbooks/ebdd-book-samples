using WIMP.App.Models;

namespace WIMP.Specs.Support;

public static class DomainDefaults
{
    public const string PizzaName = "Margherita";
    public const string PizzaSize = "Medium";
    public const PizzaStyle PizzaStyle = App.Models.PizzaStyle.Regular;

    public static PizzaItem PizzaItemDefaultInstance()
    {
        return new PizzaItem(PizzaName, PizzaSize, PizzaStyle);
    }

    public const string CustomerName = "Rebecca";
    public const string CustomerEmail = "becca@galaxy.uni";
    public const string DeliveryAddress = "2-4 Waterloo Pl, Edinburgh EH1 3EG";
    public const string AltDeliveryAddress = "18 Holyrood Park Rd, Edinburgh EH16 5AY";
}
