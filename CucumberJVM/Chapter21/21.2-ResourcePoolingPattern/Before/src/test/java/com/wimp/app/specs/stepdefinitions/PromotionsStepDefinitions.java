package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.OfferedMenuItem;
import com.wimp.app.restapi.ActivatePromotionRequest;
import com.wimp.app.specs.drivers.MenuApiDriver;
import com.wimp.app.specs.drivers.PromotionsApiDriver;
import com.wimp.app.specs.support.DataTableDiffHelper;
import com.wimp.app.specs.support.DomainDefaults;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PromotionsStepDefinitions {
    private final MenuApiDriver menuApiDriver;
    private final PromotionsApiDriver promotionsApiDriver;
    private List<OfferedMenuItem> offeredMenuItems;

    public PromotionsStepDefinitions(MenuApiDriver menuApiDriver, PromotionsApiDriver promotionsApiDriver) {
        this.menuApiDriver = menuApiDriver;
        this.promotionsApiDriver = promotionsApiDriver;
    }

    @When("the customer chooses {string} promotion")
    public void theCustomerChoosesPromotion(String promotionName) throws Exception {
        offeredMenuItems = menuApiDriver.loadMenu(promotionName, null).execute();
    }

    @Then("the following items should be offered")
    public void theFollowingItemsShouldBeOffered(DataTable expectedItemsDataTable) {
        List<OfferedMenuItem> promotionalItems = offeredMenuItems.stream()
            .filter(item -> !item.getOriginalPrice().equals(item.getPrice())).toList();
        var actualItemsTable = DataTableDiffHelper.createDataTableWithHeader(promotionalItems, expectedItemsDataTable);
        expectedItemsDataTable.unorderedDiff(actualItemsTable);
    }

    @Given("the {string} promotion is active")
    public void thePromotionIsActive(String promotionName) throws Exception {
        promotionsApiDriver.activatePromotion(new ActivatePromotionRequest(promotionName)).execute();
    }

    @Then("the customer should receive a {string} coupon via email")
    public void theCustomerShouldReceiveACouponViaEmail(String couponCode) throws Exception {
        assertTrue(promotionsApiDriver.getCoupons(DomainDefaults.CUSTOMER_EMAIL).execute().stream()
                .anyMatch(coupon -> coupon.getCode().equalsIgnoreCase(couponCode)),
            "Expected coupon '" + couponCode + "' but it was not found.");
    }
}
