using Reqnroll;

using WIMP.App.Models;

namespace WIMP.Specs.Support;

[Binding]
public class CustomParameterTypes(TestConfigurationProvider testConfigurationProvider)
{
    [StepArgumentTransformation(@"\[CUR\]([0-9\.]+)", Name = "market-currency")]
    public CurrencyValue ConvertCurrency(decimal value)
    {
        return new CurrencyValue(value,
            testConfigurationProvider.Market.Currency);
    }

    [StepArgumentTransformation(@"\[([A-Z]+)\]", Name = "market-decimal")]
    public decimal GetDecimalMarketParameter(string configKey)
    {
        return testConfigurationProvider.GetValue<decimal>("MARKET." + configKey);
    }
}
