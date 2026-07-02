using Reqnroll;

namespace WIMP.Specs.Support;

[Binding]
public class CustomParameterTypes
{
    [StepArgumentTransformation]
    public List<MenuItemData> ConvertMenuItemDataList(DataTable menuItemsTable) =>
        menuItemsTable.CreateSet<MenuItemData>().ToList();
}
