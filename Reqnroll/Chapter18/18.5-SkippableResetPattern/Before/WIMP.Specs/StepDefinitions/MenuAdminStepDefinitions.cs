using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class MenuAdminStepDefinitions(MenuBackdoorDriver menuBackdoorDriver, MenuApiDriver menuApiDriver, AuthenticationApiDriver authApiDriver)
{
    [Given("the restaurant menu is")]
    public void GivenTheRestaurantMenuIs(MenuItemData[] menuItems)
    {
        menuBackdoorDriver.SetMenuItems(menuItems);
    }

    [When("the restaurant owner adds a {string} pizza to the menu for ${decimal}")]
    public async Task WhenTheRestaurantOwnerAddsAPizzaToTheMenuFor(string name, decimal price)
    {
        await authApiDriver.Login(DomainDefaults.RestaurantOwner, DomainDefaults.Password).Execute();
        await menuApiDriver.AddMenuItem(new MenuItem { Name = name, Price = price, Calories = DomainDefaults.PizzaCalories }).Execute();
    }

    [Then("the following items should be on the menu")]
    public async Task ThenTheFollowingItemsShouldBeOnTheMenu(DataTable dataTable)
    {
        var offeredMenuItems = await menuApiDriver.LoadMenu().Execute();
        dataTable.CompareToSet(offeredMenuItems);
    }
}
