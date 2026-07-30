using Reqnroll;

namespace WIMP.Specs.Support;

[Binding]
public class CustomParameterTypes
{
    [StepArgumentTransformation]
    public MenuItemData[] ConvertMenuItems(DataTable menuDataTable)
    {
        return menuDataTable.CreateSet<MenuItemData>()!.ToArray();
    }
}
