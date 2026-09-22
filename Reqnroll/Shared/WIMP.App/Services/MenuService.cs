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

using WIMP.App.Data;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class MenuService(IDataRepository repository)
{
    public IReadOnlyCollection<OfferedMenuItem> LoadMenu(string promotionName = "", MenuFilter? filter = null)
    {
        var offeredItems = repository.GetMenuItems()
            .OrderBy(mi => mi.Name)
            .Select(mi => new OfferedMenuItem
            {
                Name = mi.Name,
                OriginalPrice = mi.Price,
                Price = mi.Price,
                Calories = mi.Calories,
                Vegetarian = mi.Vegetarian
            })
            .ToList();
        if (promotionName == "Friday $1 off")
        {
            offeredItems.ForEach(mi =>
            {
                if (mi.OriginalPrice > 8.00m)
                {
                    mi.Price = mi.OriginalPrice - 1.00m;
                }
            });
        }
        else if (promotionName == "Veggie week")
        {
            offeredItems.ForEach(mi =>
            {
                if (mi.Vegetarian)
                {
                    mi.Price = 6.00m;
                }
            });
        }

        return filter != null ? FilterMenuItems(offeredItems, filter) : offeredItems;
    }

    private IReadOnlyCollection<OfferedMenuItem> FilterMenuItems(IEnumerable<OfferedMenuItem> menuItems, MenuFilter filter)
    {
        if (filter.MinPrice != null)
        {
            menuItems = menuItems.Where(mi => mi.Price >= filter.MinPrice);
        }
        if (filter.MaxPrice != null)
        {
            menuItems = menuItems.Where(mi => mi.Price <= filter.MaxPrice);
        }
        if (filter.MaxCalories != null)
        {
            menuItems = menuItems.Where(mi => mi.Calories <= filter.MaxCalories);
        }

        return menuItems.ToArray();
    }

    public ServiceResult<MenuItem> AddMenuItem(MenuItem menuItem)
    {
        repository.InsertMenuItem(menuItem);
        return ServiceResult<MenuItem>.Success(menuItem);
    }
}
