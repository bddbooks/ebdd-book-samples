/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

using WIMP.App.Infrastructure;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class MenuService(DataContext dataContext)
{
    public void AddPizzaItem(string name, decimal price, int calories, string ingredients, bool vegetarian)
    {
        var menuItem = new MenuItem(name, price, calories, ingredients, vegetarian);
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
