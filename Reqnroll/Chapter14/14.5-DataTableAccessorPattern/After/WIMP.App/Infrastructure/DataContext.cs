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
