using Reqnroll;

using WIMP.App.Models;

namespace WIMP.Specs.Support;

[Binding]
public class SizeConverter
{
    [StepArgumentTransformation("(10\"|12\"|14\")", Name = "pizza-size")]
    public PizzaSize ConvertPizzaSize(string pizzaSizeString) =>
        pizzaSizeString switch
        {
            "10\"" => PizzaSize.Small,
            "12\"" => PizzaSize.Medium,
            "14\"" => PizzaSize.Large,
            _ => throw new FormatException($"Invalid size: {pizzaSizeString}")
        };
}
