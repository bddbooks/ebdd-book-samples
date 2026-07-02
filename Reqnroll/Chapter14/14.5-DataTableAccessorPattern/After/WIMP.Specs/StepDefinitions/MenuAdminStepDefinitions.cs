using Reqnroll;

using WIMP.App.Services;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class MenuAdminStepDefinitions(MenuService menuService)
{
    [Given("the restaurant menu is")]
    public void GivenTheRestaurantMenuIs(List<MenuItemData> menuItems)
    {
        foreach (var menuItem in menuItems)
        {
            menuService.AddPizzaItem(
                menuItem.Name,
                menuItem.Price ?? DomainDefaults.MenuItemPrice,
                menuItem.Calories ?? DomainDefaults.MenuItemCalories,
                menuItem.Ingredients ?? DomainDefaults.MenuItemIngredients);
        }
    }
}
