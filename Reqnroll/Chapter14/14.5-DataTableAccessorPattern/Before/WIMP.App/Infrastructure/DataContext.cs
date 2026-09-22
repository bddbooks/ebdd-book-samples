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

using WIMP.App.Models;

namespace WIMP.App.Infrastructure;

public class DataContext
{
    private readonly List<MenuItem> menuItems = new();

    private void InsertOrUpdate<TEntity>(List<TEntity> list, TEntity entity, Predicate<TEntity> match)
    {
        int foundIndex = list.FindIndex(match);

        if (foundIndex < 0)
        {
            list.Add(entity);
        }
        else
        {
            list[foundIndex] = entity;
        }
    }

    public void SaveMenuItem(MenuItem menuItem)
    {
        InsertOrUpdate(menuItems, menuItem, mi => mi.Name == menuItem.Name);
    }

    public MenuItem? GetMenuItem(string name) =>
        menuItems.FirstOrDefault(mi => mi.Name == name);

    public IReadOnlyCollection<MenuItem> GetMenuItems() =>
        menuItems.ToArray();
}
