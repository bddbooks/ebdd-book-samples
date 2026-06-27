namespace WIMP.App.Models;

public class PizzaItem
{
    public string Name { get; set; } = "Margherita";
    public PizzaSize Size { get; set; } = PizzaSize.Medium;
    public PizzaStyle Style { get; set; } = PizzaStyle.Regular;
}
