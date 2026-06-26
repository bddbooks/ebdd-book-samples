namespace WIMP.App.Models;

public class PizzaItem(string name, PizzaSize size, PizzaStyle style)
{
    public string Name { get; set; } = name;
    public PizzaSize Size { get; set; } = size;
    public PizzaStyle Style { get; set; } = style;
}
