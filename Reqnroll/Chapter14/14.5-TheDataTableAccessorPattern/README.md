# Pattern Differences: 14.5-TheDataTableAccessorPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/StepDefinitions/MenuAdminStepDefinitions.cs](#wimpspecsstepdefinitionsmenuadminstepdefinitionscs)
- ➕ Added [WIMP.Specs/Support/CustomParameterTypes.cs](#wimpspecssupportcustomparametertypescs)
- ➕ Added [WIMP.Specs/Support/MenuItemData.cs](#wimpspecssupportmenuitemdatacs)

## Detailed Changes

### WIMP.Specs/StepDefinitions/MenuAdminStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/MenuAdminStepDefinitions.cs#L9)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/MenuAdminStepDefinitions.cs#L12-L20)</sub>

```diff
@@ -9,22 +9,15 @@ namespace WIMP.Specs.StepDefinitions;
 public class MenuAdminStepDefinitions(MenuService menuService)
 {
     [Given("the restaurant menu is")]
-    public void GivenTheRestaurantMenuIs(DataTable menuItemsTable)
+    public void GivenTheRestaurantMenuIs(List<MenuItemData> menuItems)
     {
-        foreach (var row in menuItemsTable.Rows)
+        foreach (var menuItem in menuItems)
         {
-            string name = row["name"];
-            decimal price = menuItemsTable.ContainsColumn("price")
-                ? decimal.Parse(row["price"])
-                : DomainDefaults.MenuItemPrice;
-            int calories = menuItemsTable.ContainsColumn("calories")
-                ? int.Parse(row["calories"])
-                : DomainDefaults.MenuItemCalories;
-            string ingredients = menuItemsTable.ContainsColumn("ingredients")
-                ? row["ingredients"]
-                : DomainDefaults.MenuItemIngredients;
-
-            menuService.AddPizzaItem(name, price, calories, ingredients);
+            menuService.AddPizzaItem(
+                menuItem.Name,
+                menuItem.Price ?? DomainDefaults.MenuItemPrice,
+                menuItem.Calories ?? DomainDefaults.MenuItemCalories,
+                menuItem.Ingredients ?? DomainDefaults.MenuItemIngredients);
         }
     }
 }
```

### WIMP.Specs/Support/CustomParameterTypes.cs

[View file](After/WIMP.Specs/Support/CustomParameterTypes.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/CustomParameterTypes.cs#L1-L11)</sub>

```diff
@@ -0,0 +1,11 @@
+using Reqnroll;
+
+namespace WIMP.Specs.Support;
+
+[Binding]
+public class CustomParameterTypes
+{
+    [StepArgumentTransformation]
+    public List<MenuItemData> ConvertMenuItemDataList(DataTable menuItemsTable) =>
+        menuItemsTable.CreateSet<MenuItemData>().ToList();
+}
```

### WIMP.Specs/Support/MenuItemData.cs

[View file](After/WIMP.Specs/Support/MenuItemData.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/MenuItemData.cs#L1-L9)</sub>

```diff
@@ -0,0 +1,9 @@
+namespace WIMP.Specs.Support;
+
+public class MenuItemData
+{
+    public required string Name { get; set; }
+    public decimal? Price { get; set; }
+    public int? Calories { get; set; }
+    public string? Ingredients { get; set; }
+}
```
