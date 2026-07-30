using Reqnroll;

using WIMP.App.Data;
using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

[Binding]
public class Hooks(AppHostingContext appHostingContext, IDatabaseDriver databaseDriver, IDataRepository dataRepository, MenuBackdoorDriver menuBackdoorDriver)
{
    [BeforeTestRun]
    public static void InitializeDatabase()
    {
        var testConfigurationProvider = new TestConfigurationProvider();
        if (!testConfigurationProvider.Database.UseStub)
        {
            DatabaseDriver.UpgradeSchemaIfNeeded(testConfigurationProvider.Database.ConnectionString);
        }
    }

    [BeforeScenario(Order = 0)]
    public async Task ResetDatabase()
    {
        await databaseDriver.EmptyDatabase();
        SeedMenuData();
    }

    private void SeedMenuData()
    {
        Console.WriteLine("seeding menu data");
        menuBackdoorDriver.SetMenuItems(
        [
            new MenuItemData { Name = "Margherita", Price = 7.99m, Calories = 900, Vegetarian = true },
            new MenuItemData { Name = "Pepperoni", Price = 9.99m, Calories = 1200 },
            new MenuItemData { Name = "Capricciosa", Price = 8.99m, Calories = 1100 }
        ]);
    }

    [BeforeScenario(Order = 1)]
    public void CreateAppHost()
    {
        appHostingContext.AppHost = new WimpAppHost(dataRepository);
    }

    [AfterScenario]
    public void DisposeAppHost()
    {
        appHostingContext.AppHost?.Dispose();
    }
}
