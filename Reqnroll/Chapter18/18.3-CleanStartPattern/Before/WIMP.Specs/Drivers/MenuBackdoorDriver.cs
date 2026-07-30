using WIMP.App.Data;
using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class MenuBackdoorDriver(IDataRepository dataRepository)
{
    public void SetMenuItems(IEnumerable<MenuItemData> menuItemDataList)
    {
        var itemsToSet = menuItemDataList
            .Select(ToMenuItem)
            .ToList();
        dataRepository.SetMenuItems(itemsToSet);
    }

    public void AddMenuItem(MenuItemData menuItemData)
    {
        dataRepository.InsertMenuItem(ToMenuItem(menuItemData));
    }

    private MenuItem ToMenuItem(MenuItemData item)
    {
        return new MenuItem
        {
            Name = item.Name,
            Price = item.Price ?? DomainDefaults.PizzaPrice,
            Calories = item.Calories ?? DomainDefaults.PizzaCalories,
            Vegetarian = item.Vegetarian
        };
    }

    public IReadOnlyCollection<MenuItem> GetMenuItems()
    {
        return dataRepository.GetMenuItems()
            .OrderBy(mi => mi.Name)
            .ToArray();
    }
}
