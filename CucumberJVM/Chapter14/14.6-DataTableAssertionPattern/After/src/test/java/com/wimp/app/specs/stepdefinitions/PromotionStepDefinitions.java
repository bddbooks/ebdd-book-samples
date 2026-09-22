package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.OfferedItem;
import com.wimp.app.models.Promotion;
import com.wimp.app.services.PromotionService;
import com.wimp.app.specs.support.DataTableDiffHelper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.util.List;

public class PromotionStepDefinitions {

    private final PromotionService promotionService;
    private Promotion activePromotion;

    public PromotionStepDefinitions(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @When("the customer chooses {string} promotion")
    public void theCustomerChoosesPromotion(String promotionName) {
        activePromotion = promotionService.activatePromotion(promotionName);
    }

    @Then("the following items should be offered")
    public void theFollowingItemsShouldBeOffered(DataTable expectedItemsDataTable) {
        List<OfferedItem> promotionalItems = activePromotion.offeredItems().stream()
            .filter(item -> !item.originalPrice().equals(item.price())).toList();
        var actualItemsTable = DataTableDiffHelper.createDataTableWithHeader(promotionalItems, expectedItemsDataTable);
        expectedItemsDataTable.unorderedDiff(actualItemsTable);
    }
}
