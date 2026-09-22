# Pattern Differences: 14.5-DataTableAccessorPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/MenuAdminStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsmenuadminstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java](#srctestjavacomwimpappspecssupportcustomparametertypesjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/MenuItemData.java](#srctestjavacomwimpappspecssupportmenuitemdatajava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/MenuAdminStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/MenuAdminStepDefinitions.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/MenuAdminStepDefinitions.java#L4-L7)</sub>

```diff
@@ -1,11 +1,10 @@
 package com.wimp.app.specs.stepdefinitions;
 
 import com.wimp.app.services.MenuService;
-import com.wimp.app.specs.support.DomainDefaults;
-import io.cucumber.datatable.DataTable;
+import com.wimp.app.specs.support.MenuItemData;
 import io.cucumber.java.en.*;
 
-import java.math.BigDecimal;
+import java.util.List;
 
 public class MenuAdminStepDefinitions {
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/MenuAdminStepDefinitions.java#L18-L20)</sub>

```diff
@@ -16,20 +15,9 @@ public class MenuAdminStepDefinitions {
     }
 
     @Given("the restaurant menu is")
-    public void theRestaurantMenuIs(DataTable menuItemsTable) {
-        for (var row : menuItemsTable.asMaps()) {
-            var name = row.get("name");
-            var price = row.containsKey("price")
-                ? new BigDecimal(row.get("price"))
-                : DomainDefaults.PIZZA_PRICE;
-            var calories = row.containsKey("calories")
-                ? Integer.parseInt(row.get("calories"))
-                : DomainDefaults.PIZZA_CALORIES;
-            var ingredients = row.containsKey("ingredients")
-                ? row.get("ingredients")
-                : DomainDefaults.PIZZA_INGREDIENTS;
-
-            menuService.addMenuItem(name, price, calories, ingredients);
+    public void theRestaurantMenuIs(List<MenuItemData> menuItems) {
+        for (var row : menuItems) {
+            menuService.addMenuItem(row.name(), row.price(), row.calories(), row.ingredients());
         }
     }
 }
```

### src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java

[View file](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L3-L19)</sub>

```diff
@@ -1,11 +1,22 @@
 package com.wimp.app.specs.support;
 
+import io.cucumber.java.DataTableType;
 import io.cucumber.java.ParameterType;
 
 import java.math.BigDecimal;
+import java.util.Map;
 
 public class CustomParameterTypes {
 
+    @DataTableType
+    public MenuItemData menuItemDataRow(Map<String, String> row) {
+        return new MenuItemData(
+            row.get("name"),
+            row.containsKey("price") ? new BigDecimal(row.get("price")) : DomainDefaults.PIZZA_PRICE,
+            row.containsKey("calories") ? Integer.parseInt(row.get("calories")) : DomainDefaults.PIZZA_CALORIES,
+            row.getOrDefault("ingredients", DomainDefaults.PIZZA_INGREDIENTS));
+    }
+
     @ParameterType("\\$?(\\d+(?:\\.\\d{1,2})?)")
     public BigDecimal price(String value){
         return new BigDecimal(value);
```

### src/test/java/com/wimp/app/specs/support/MenuItemData.java

[View file](After/src/test/java/com/wimp/app/specs/support/MenuItemData.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/MenuItemData.java#L1-L6)</sub>

```diff
@@ -0,0 +1,6 @@
+package com.wimp.app.specs.support;
+
+import java.math.BigDecimal;
+
+public record MenuItemData(String name, BigDecimal price, Integer calories, String ingredients) {
+}
```
