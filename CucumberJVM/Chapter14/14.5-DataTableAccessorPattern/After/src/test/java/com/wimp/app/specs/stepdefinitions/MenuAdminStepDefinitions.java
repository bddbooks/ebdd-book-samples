package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.services.MenuService;
import com.wimp.app.specs.support.MenuItemData;
import io.cucumber.java.en.*;

import java.util.List;

public class MenuAdminStepDefinitions {

    private final MenuService menuService;

    public MenuAdminStepDefinitions(MenuService menuService) {
        this.menuService = menuService;
    }

    @Given("the restaurant menu is")
    public void theRestaurantMenuIs(List<MenuItemData> menuItems) {
        for (var row : menuItems) {
            menuService.addMenuItem(row.name(), row.price(), row.calories(), row.ingredients());
        }
    }
}
