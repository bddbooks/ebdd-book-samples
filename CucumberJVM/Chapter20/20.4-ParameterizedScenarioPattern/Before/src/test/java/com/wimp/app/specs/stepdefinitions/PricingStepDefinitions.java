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

    @Given("the net price of the {word} pizza is ${decimal}")
    public void theNetPriceOfThePizzaIs(String pizzaName, BigDecimal netUnitPrice) {
        pizzaNetPrices.put(pizzaName, new CurrencyValue(netUnitPrice, "USD"));
    }

    @When("the price of {int} {word} pizzas are calculated")
    public void thePriceOfPizzasAreCalculated(int count, String pizzaName) {
        calculatedPrice = pricingService.calculateItemPrice(pizzaNetPrices.get(pizzaName), count);
    }

    @Then("the sum net price is ${decimal}")
    public void theSumNetPriceIs(BigDecimal expectedPrice) {
        assertThat(calculatedPrice == null ? null : calculatedPrice.netPrice()).isEqualByComparingTo(new CurrencyValue(expectedPrice, "USD"));
    }

    @Then("the sales tax is {decimal} percent")
    public void theSalesTaxIsPercent(BigDecimal expectedPercent) {
        assertThat(calculatedPrice == null ? null : calculatedPrice.salesTaxPercent()).isEqualByComparingTo(expectedPercent);
    }
}
