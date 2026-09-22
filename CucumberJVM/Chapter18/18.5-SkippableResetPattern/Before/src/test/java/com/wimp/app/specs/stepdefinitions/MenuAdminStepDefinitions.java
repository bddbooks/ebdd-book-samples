package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.MenuItem;
import com.wimp.app.models.OfferedMenuItem;
import com.wimp.app.specs.drivers.AuthenticationApiDriver;
import com.wimp.app.specs.drivers.MenuApiDriver;
import com.wimp.app.specs.drivers.MenuBackdoorDriver;
import com.wimp.app.specs.support.DataTableDiffHelper;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.MenuItemData;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

public class MenuAdminStepDefinitions {
    private final MenuBackdoorDriver menuBackdoorDriver;
    private final MenuApiDriver menuApiDriver;
    private final AuthenticationApiDriver authenticationApiDriver;

    public MenuAdminStepDefinitions(MenuBackdoorDriver menuBackdoorDriver, MenuApiDriver menuApiDriver,
                                    AuthenticationApiDriver authenticationApiDriver) {
        this.menuBackdoorDriver = menuBackdoorDriver;
        this.menuApiDriver = menuApiDriver;
        this.authenticationApiDriver = authenticationApiDriver;
    }

    @Given("the restaurant menu is")
    public void theRestaurantMenuIs(List<MenuItemData> menuItems) { menuBackdoorDriver.setMenuItems(menuItems); }

    @When("the restaurant owner adds a {string} pizza to the menu for {price}")
    public void theRestaurantOwnerAddsAPizzaToTheMenuFor(String name, BigDecimal price) throws Exception {
        authenticationApiDriver.login(DomainDefaults.RESTAURANT_OWNER, DomainDefaults.PASSWORD).execute();
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setPrice(price);
        item.setCalories(DomainDefaults.PIZZA_CALORIES);
        menuApiDriver.addMenuItem(item).execute();
    }

    @Then("the following items should be on the menu")
    public void theFollowingItemsShouldBeOnTheMenu(DataTable expectedItemsDataTable) throws Exception {
        List<OfferedMenuItem> actual = menuApiDriver.loadMenu().execute();
        DataTable actualItemsTable = DataTableDiffHelper.createDataTableWithHeader(actual, expectedItemsDataTable);
        expectedItemsDataTable.unorderedDiff(actualItemsTable);
    }
}
