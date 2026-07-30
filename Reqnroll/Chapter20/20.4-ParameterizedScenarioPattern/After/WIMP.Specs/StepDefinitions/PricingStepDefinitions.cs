using Reqnroll;

using WIMP.App.Models;
using WIMP.App.Services;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class PricingStepDefinitions(PricingService pricingService, IReqnrollOutputHelper outputHelper)
{
    private readonly Dictionary<string, CurrencyValue> pizzaNetPrices = new();
    private OrderItemPrice? calculatedPrice;

    [Given("the net price of the {word} pizza is {market-currency}")]
    public void GivenTheNetPriceOfThePizzaIs(string pizzaName, CurrencyValue netUnitPrice)
    {
        pizzaNetPrices[pizzaName] = netUnitPrice;
    }

    [When("the price of {int} {word} pizzas are calculated")]
    public void WhenThePriceOfPizzasAreCalculated(int count, string pizzaName)
    {
        var netUnitPrice = pizzaNetPrices[pizzaName];
        calculatedPrice = pricingService.CalculateItemPrice(netUnitPrice, count);
        outputHelper.WriteLine($"Calculated price: {calculatedPrice}");
    }

    [Then("the sum net price is {market-currency}")]
    public void ThenTheSumNetPriceIs(CurrencyValue expectedPrice)
    {
        Assert.AreEqual(expectedPrice, calculatedPrice?.NetPrice);
    }

    [Then("the sales tax is {market-decimal} percent")]
    public void ThenTheSalesTaxIsPercent(decimal expectedSalesTaxPercent)
    {
        Assert.AreEqual(expectedSalesTaxPercent, calculatedPrice?.SalesTaxPercent);
    }
}
