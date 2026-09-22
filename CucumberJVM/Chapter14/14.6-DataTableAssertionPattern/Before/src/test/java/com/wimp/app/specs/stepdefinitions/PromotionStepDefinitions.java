package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.Promotion;
import com.wimp.app.services.PromotionService;
import com.wimp.app.specs.support.OfferedItemData;
import io.cucumber.java.en.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
    public void theFollowingItemsShouldBeOffered(List<OfferedItemData> expectedOfferedItems) {
        var offeredItems = Optional.of(activePromotion.offeredItems()).orElseThrow(() -> new RuntimeException("No active promotion"));

        int itemsToCompare = Math.min(offeredItems.size(), expectedOfferedItems.size());
        for (int i = 0; i < itemsToCompare; i++)
        {
            var expectedItem = expectedOfferedItems.get(i);
            var actualItem = offeredItems.get(i);

            assertEquals(expectedItem.name(), actualItem.name());
            if (expectedItem.price() != null)
            {
                assertTrue(expectedItem.price().compareTo(actualItem.price()) == 0);
            }

            if (expectedItem.originalPrice() != null)
            {
                assertTrue(expectedItem.originalPrice().compareTo(actualItem.originalPrice()) == 0);
            }
        }

        assertEquals(expectedOfferedItems.size(), offeredItems.size(),
            "The offered item count is different from the expected");
    }
}
