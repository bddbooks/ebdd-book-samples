namespace WIMP.App.Models;

public class PizzaItem(string name, string size, PizzaStyle style)
{
    public string Name { get; set; } = name;
    public string Size { get; set; } = size;
    public PizzaStyle Style { get; set; } = style;
}
