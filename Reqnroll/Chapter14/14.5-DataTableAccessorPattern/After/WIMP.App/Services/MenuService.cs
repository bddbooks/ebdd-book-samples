using WIMP.App.Infrastructure;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class MenuService(DataContext dataContext)
{
    public void AddPizzaItem(string name, decimal price, int calories, string ingredients)
    {
        var menuItem = new MenuItem(name, price, calories, ingredients);
        dataContext.SaveMenuItem(menuItem);
    }

    public IReadOnlyCollection<MenuItem> GetItems()
    {
        return dataContext.GetMenuItems().OrderBy(mi => mi.Name).ToArray();
    }

    public IReadOnlyCollection<MenuItem> GetFilteredItems(decimal? minPrice = null, decimal? maxPrice = null, int? maxCalories = null)
    {
        var menuItems = GetItems().AsEnumerable();
        if (minPrice != null)
        {
            menuItems = menuItems.Where(mi => mi.Price >= minPrice);
        }
        if (maxPrice != null)
        {
            menuItems = menuItems.Where(mi => mi.Price <= maxPrice);
        }
        if (maxCalories != null)
        {
            menuItems = menuItems.Where(mi => mi.Calories <= maxCalories);
        }

        return menuItems.ToArray();
    }
}
