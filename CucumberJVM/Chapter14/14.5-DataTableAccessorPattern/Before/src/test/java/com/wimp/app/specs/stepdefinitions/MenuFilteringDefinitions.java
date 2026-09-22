package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.MenuItem;
import com.wimp.app.services.MenuService;
import io.cucumber.java.en.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MenuFilteringDefinitions {

    private final MenuService menuService;
    private List<MenuItem> filteredMenuItems;

    public MenuFilteringDefinitions(MenuService menuService) {
        this.menuService = menuService;
    }

    @When("the customer filters the menu for price range between {price} and {price}")
    public void theCustomerFiltersTheMenuForPriceRangeBetweenAnd(BigDecimal minPrice, BigDecimal maxPrice) {
        filteredMenuItems = menuService.getFilteredItems(minPrice, maxPrice, null);
    }

    @When("the customer filters the menu for maximum calories {int}")
    public void theCustomerFiltersTheMenuForMaximumCalories(int maxCalories) {
        filteredMenuItems = menuService.getFilteredItems(null, null, maxCalories);
    }

    @Then("the filtered result should contain only the pizza item {string}")
    public void theFilteredResultShouldContainOnlyThePizzaItem(String expectedPizzaName) {
        assertNotNull(filteredMenuItems);
        assertEquals(1, filteredMenuItems.size());
        assertEquals(expectedPizzaName, filteredMenuItems.getFirst().getName());
    }
}
