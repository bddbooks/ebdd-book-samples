using Reqnroll;

using WIMP.App.Models;
using WIMP.App.Services;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class MenuFilteringStepDefinitions(MenuService menuService)
{
    private IReadOnlyCollection<MenuItem>? filteredMenuItems;

    [When("the customer filters the menu for price range between {float} and {float}")]
    public void WhenTheCustomerFiltersTheMenuForPriceRangeBetweenAnd(decimal minPrice, decimal maxPrice)
    {
        filteredMenuItems = menuService.GetFilteredItems(minPrice: minPrice, maxPrice: maxPrice);
    }

    [When("the customer filters the menu for maximum calories {int}")]
    public void WhenTheCustomerFiltersTheMenuForMaximumCalories(int maxCalories)
    {
        filteredMenuItems = menuService.GetFilteredItems(maxCalories: maxCalories);
    }

    [Then("the filtered result should contain only the pizza item {string}")]
    public void ThenTheFilteredResultShouldContainOnlyThePizzaItem(string expectedPizzaName)
    {
        Assert.IsNotNull(filteredMenuItems);
        Assert.HasCount(1, filteredMenuItems);
        Assert.AreEqual(expectedPizzaName, filteredMenuItems.First().Name);
    }
}
