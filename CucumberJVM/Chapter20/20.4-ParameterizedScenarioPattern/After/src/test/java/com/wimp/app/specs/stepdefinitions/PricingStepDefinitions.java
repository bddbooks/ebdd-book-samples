package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.CurrencyValue;
import com.wimp.app.models.OrderItemPrice;
import com.wimp.app.services.PricingService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PricingStepDefinitions {
    private final PricingService pricingService;
    private final Map<String, CurrencyValue> pizzaNetPrices = new HashMap<>();
    private OrderItemPrice calculatedPrice;

    public PricingStepDefinitions(PricingService pricingService) { this.pricingService = pricingService; }

    @Given("the net price of the {word} pizza is {market-currency}")
    public void theNetPriceOfThePizzaIs(String pizzaName, CurrencyValue netUnitPrice) {
        pizzaNetPrices.put(pizzaName, netUnitPrice);
    }

    @When("the price of {int} {word} pizzas are calculated")
    public void thePriceOfPizzasAreCalculated(int count, String pizzaName) {
        calculatedPrice = pricingService.calculateItemPrice(pizzaNetPrices.get(pizzaName), count);
    }

    @Then("the sum net price is {market-currency}")
    public void theSumNetPriceIs(CurrencyValue expectedPrice) {
        assertThat(calculatedPrice == null ? null : calculatedPrice.netPrice()).isEqualByComparingTo(expectedPrice);
    }

    @Then("the sales tax is {market-decimal} percent")
    public void theSalesTaxIsPercent(BigDecimal expectedPercent) {
        assertThat(calculatedPrice == null ? null : calculatedPrice.salesTaxPercent()).isEqualByComparingTo(expectedPercent);
    }
}
