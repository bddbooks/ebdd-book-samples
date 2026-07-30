using WIMP.App.Models;

namespace WIMP.Specs.Support;

public static class DomainDefaults
{
    public const string CustomerName = "Rebecca";
    public const string Password = "Pa22w0rd!";
    public const string CustomerEmail = "rebecca@example.com";
    public const string CustomerAddress = "2850 Piedmont Ave NE, Apt 3C, Atlanta, GA 30308";
    public const string KitchenStaff = "Kitchen1";

    public static PizzaItem PizzaItemDefaultInstance(
        string? name = null, PizzaSize? size = null, PizzaStyle? style = null)
    {
        return new PizzaItem
        {
            Name = name ?? PizzaName,
            Size = size ?? PizzaSize,
            Style = style ?? PizzaStyle
        };
    }

    public const string PizzaName = "Margherita";
    public const PizzaSize PizzaSize = App.Models.PizzaSize.Medium;
    public const PizzaStyle PizzaStyle = WIMP.App.Models.PizzaStyle.Regular;
}
