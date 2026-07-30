using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class MenuAdminStepDefinitions(MenuBackdoorDriver menuBackdoorDriver)
{
    [Given("the restaurant menu is")]
    public void GivenTheRestaurantMenuIs(MenuItemData[] menuItems)
    {
        menuBackdoorDriver.SetMenuItems(menuItems);
    }
}
