using Reqnroll;

using WIMP.App.Models;
using WIMP.App.Services;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class PricingStepDefinitions(PricingService pricingService, IReqnrollOutputHelper outputHelper)
{
    private readonly Dictionary<string, CurrencyValue> pizzaNetPrices = new();
    private OrderItemPrice? calculatedPrice;

    [Given("the net price of the {word} pizza is ${decimal}")]
    public void GivenTheNetPriceOfThePizzaIs(string pizzaName, decimal netUnitPrice)
    {
        pizzaNetPrices[pizzaName] = new CurrencyValue(netUnitPrice, "USD");
    }

    [When("the price of {int} {word} pizzas are calculated")]
    public void WhenThePriceOfPizzasAreCalculated(int count, string pizzaName)
    {
        var netUnitPrice = pizzaNetPrices[pizzaName];
        calculatedPrice = pricingService.CalculateItemPrice(netUnitPrice, count);
        outputHelper.WriteLine($"Calculated price: {calculatedPrice}");
    }

    [Then("the sum net price is ${decimal}")]
    public void ThenTheSumNetPriceIs(decimal expectedPrice)
    {
        Assert.AreEqual(expectedPrice, calculatedPrice?.NetPrice.Value);
    }

    [Then("the sales tax is {decimal} percent")]
    public void ThenTheSalesTaxIsPercent(decimal expectedSalesTaxPercent)
    {
        Assert.AreEqual(expectedSalesTaxPercent, calculatedPrice?.SalesTaxPercent);
    }
}
