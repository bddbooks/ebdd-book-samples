# Pattern Differences: 18.4-SeededDataPattern

This document shows the differences between the Before and After implementations of this pattern.

## Preparation steps for this sample

This sample requires a MySQL database to be running. The following instructions are for setting up a MySQL database in a Docker container.

* Create and start container (first time):  
  `docker run --name wimp-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=wimp_db -p 3306:3306 -d mysql:8.4`

* Stop container:  
  `docker stop wimp-mysql`

* Start existing container again:  
  `docker start wimp-mysql`

* Restart container:  
  `docker restart wimp-mysql`

* Start from scratch (delete container + all DB data in it):
  ```
  docker stop wimp-mysql
  docker rm wimp-mysql
  docker run --name wimp-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=wimp_db -p 3306:3306 -d mysql:8.4
  ```


## Summary of Changes

- 📝 Modified [WIMP.Specs/Features/Ordering.feature](#wimpspecsfeaturesorderingfeature)
- 📝 Modified [WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpspecsstepdefinitionsorderingstepdefinitionscs)
- 📝 Modified [WIMP.Specs/Support/Hooks.cs](#wimpspecssupporthookscs)

## Detailed Changes

### WIMP.Specs/Features/Ordering.feature

[View file](After/WIMP.Specs/Features/Ordering.feature#L2)

<sub>[Jump to change](After/WIMP.Specs/Features/Ordering.feature#L5)</sub>

```diff
@@ -2,7 +2,7 @@
 
 Rule: A 25% discount coupon is sent for the next purchase when Margherita pizza is ordered on a "Margherita Friday"
 
-  # This scenario assumes that a pizza named "Margherita" exists, that is ensured by the step definition of 'And the customer has placed an order...'
+  # This scenario assumes that a pizza named "Margherita" exists, that is ensured by seeded data
   Scenario: A Margherita Friday coupon is sent
     Given the "Margherita Friday" promotion is active
     And the customer has placed an order containing a "Margherita" pizza
```

### WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L6)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L9)</sub>

```diff
@@ -6,20 +6,13 @@ using WIMP.Specs.Support;
 namespace WIMP.Specs.StepDefinitions;
 
 [Binding]
-public class OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, AuthenticationApiDriver authApiDriver, MenuBackdoorDriver menuBackdoorDriver)
+public class OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, AuthenticationApiDriver authApiDriver)
 {
     private int? placedOrderNo;
 
     [Given("the customer has placed an order containing a {string} pizza")]
     public async Task GivenTheCustomerHasPlacedAnOrderContainingAPizza(string pizzaName)
     {
-        // ensuring that the pizza is on the menu
-        var menu = menuBackdoorDriver.GetMenuItems();
-        if (menu.FirstOrDefault(mi => mi.Name == pizzaName) is null)
-        {
-            menuBackdoorDriver.AddMenuItem(new MenuItemData { Name = pizzaName });
-        }
-
         await authApiDriver
             .Login(DomainDefaults.CustomerName, DomainDefaults.Password)
             .Execute();
```

### WIMP.Specs/Support/Hooks.cs

[View file](After/WIMP.Specs/Support/Hooks.cs#L6)

<sub>[Jump to change](After/WIMP.Specs/Support/Hooks.cs#L9-L27)</sub>

```diff
@@ -6,13 +6,25 @@ using WIMP.Specs.Drivers;
 namespace WIMP.Specs.Support;
 
 [Binding]
-public class Hooks(AppHostingContext appHostingContext, IDatabaseDriver databaseDriver, IDataRepository dataRepository)
+public class Hooks(AppHostingContext appHostingContext, IDatabaseDriver databaseDriver, IDataRepository dataRepository, MenuBackdoorDriver menuBackdoorDriver)
 {
     [BeforeScenario(Order = 0)]
     public async Task ResetDatabase()
     {
         await databaseDriver.UpgradeSchemaIfNeeded();
         await databaseDriver.EmptyDatabase();
+        SeedMenuData();
+    }
+
+    private void SeedMenuData()
+    {
+        Console.WriteLine("seeding menu data");
+        menuBackdoorDriver.SetMenuItems(
+        [
+            new MenuItemData { Name = "Margherita", Price = 7.99m, Calories = 900, Vegetarian = true },
+            new MenuItemData { Name = "Pepperoni", Price = 9.99m, Calories = 1200 },
+            new MenuItemData { Name = "Capricciosa", Price = 8.99m, Calories = 1100 }
+        ]);
     }
 
     [BeforeScenario(Order = 1)]
```
