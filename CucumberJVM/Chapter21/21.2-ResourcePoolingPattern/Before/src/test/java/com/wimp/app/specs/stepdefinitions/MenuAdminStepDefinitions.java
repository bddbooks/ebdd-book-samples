package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.specs.drivers.MenuBackdoorDriver;
import com.wimp.app.specs.support.MenuItemData;
import io.cucumber.java.en.Given;

import java.util.List;

public class MenuAdminStepDefinitions {
    private final MenuBackdoorDriver menuBackdoorDriver;

    public MenuAdminStepDefinitions(MenuBackdoorDriver menuBackdoorDriver) {
        this.menuBackdoorDriver = menuBackdoorDriver;
    }

    @Given("the restaurant menu is")
    public void theRestaurantMenuIs(List<MenuItemData> menuItems) { menuBackdoorDriver.setMenuItems(menuItems); }
}
