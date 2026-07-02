using Reqnroll;

using WIMP.App.Services;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class MenuAdminStepDefinitions(MenuService menuService)
{
    [Given("the restaurant menu is")]
    public void GivenTheRestaurantMenuIs(DataTable menuItemsTable)
    {
        foreach (var row in menuItemsTable.Rows)
        {
            string name = row["name"];
            decimal price = menuItemsTable.ContainsColumn("price")
                ? decimal.Parse(row["price"])
                : DomainDefaults.MenuItemPrice;
            int calories = menuItemsTable.ContainsColumn("calories")
                ? int.Parse(row["calories"])
                : DomainDefaults.MenuItemCalories;
            string ingredients = menuItemsTable.ContainsColumn("ingredients")
                ? row["ingredients"]
                : DomainDefaults.MenuItemIngredients;

            menuService.AddPizzaItem(name, price, calories, ingredients);
        }
    }
}
