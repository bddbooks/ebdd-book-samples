package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.services.MenuService;
import com.wimp.app.specs.support.DomainDefaults;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.math.BigDecimal;

public class MenuAdminStepDefinitions {

    private final MenuService menuService;

    public MenuAdminStepDefinitions(MenuService menuService) {
        this.menuService = menuService;
    }

    @Given("the restaurant menu is")
    public void theRestaurantMenuIs(DataTable menuItemsTable) {
        for (var row : menuItemsTable.asMaps()) {
            var name = row.get("name");
            var price = row.containsKey("price")
                ? new BigDecimal(row.get("price"))
                : DomainDefaults.PIZZA_PRICE;
            var calories = row.containsKey("calories")
                ? Integer.parseInt(row.get("calories"))
                : DomainDefaults.PIZZA_CALORIES;
            var ingredients = row.containsKey("ingredients")
                ? row.get("ingredients")
                : DomainDefaults.PIZZA_INGREDIENTS;

            menuService.addMenuItem(name, price, calories, ingredients);
        }
    }
}
