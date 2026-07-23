using System.Globalization;

using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Drivers;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class ReportingStepDefinitions(
    ReportingApiDriver reportingApiDriver,
    BackdoorApiDriver backdoorApiDriver)
{
    private SalesReport? generatedReport;

    [Given("a normal week of operations with these pizza sales:")]
    public async Task GivenANormalWeekOfOperationsWithThesePizzaSales(DataTable salesTable)
    {
        decimal ParseMoneyValue(string value)
        {
            return decimal.Parse(value.TrimStart('$'), CultureInfo.InvariantCulture);
        }

        var salesTraffic = salesTable.Rows.SelectMany(row =>
                row.Where(r => r.Key != "date")
                    .Select(c => new DailyPizzaSales
                    {
                        Date = DateOnly.Parse(row["date"]),
                        Pizza = c.Key,
                        Sales = string.IsNullOrEmpty(c.Value) ? 0m : ParseMoneyValue(c.Value)
                    }))
            .ToList();

        await backdoorApiDriver.PrepareSalesTraffic(salesTraffic).Execute();
    }

    [When("the sales report is requested for the week beginning {DateOnly}")]
    public async Task WhenTheSalesReportIsRequestedForTheWeekBeginning(DateOnly startDay)
    {
        generatedReport = await reportingApiDriver.GenerateSalesReport(startDay).Execute();
    }

    [Then("the report should show a total sales volume of ${int}")]
    public void ThenTheReportShouldShowATotalSalesVolumeOf(decimal expectedTotalSales)
    {
        Assert.IsNotNull(generatedReport, "Report is not generated.");
        Assert.AreEqual(expectedTotalSales, generatedReport.TotalSales.Sales, "Unexpected total sales.");
    }

    [Then("there should be ${int} Pepperoni, ${int} Margherita, and ${int} BBQ sales on the report")]
    public void ThenThereShouldBePizzaSalesOnTheReport(
        decimal pepperoniSales, decimal margheritaSales, decimal bbqSales)
    {
        void AssertPizzaSales(string pizzaName, decimal expectedSales)
        {
            Assert.IsTrue(
                generatedReport!.SalesByPizza.TryGetValue(pizzaName, out var pizzaValue),
                $"Sales data for pizza '{pizzaName}' was not found in the report.");
            Assert.AreEqual(expectedSales, pizzaValue!.Sales, $"Unexpected sales for '{pizzaName}'.");
        }

        Assert.IsNotNull(generatedReport, "Expected a generated report but it was null.");
        AssertPizzaSales("Pepperoni", pepperoniSales);
        AssertPizzaSales("Margherita", margheritaSales);
        AssertPizzaSales("BBQ", bbqSales);
    }

    [Then("there should be a by day breakdown on the report")]
    public void ThenThereShouldBeAByDayBreakdownOnTheReport()
    {
        Assert.IsNotNull(generatedReport, "Expected a generated report but it was null.");
        Assert.HasCount(7, generatedReport!.SalesByDay, $"Expected sales data for 7 days but found {generatedReport.SalesByDay.Count}.");
    }

    [Then("all values should be also shown as percentages of the total")]
    public void ThenAllValuesShouldBeAlsoShownAsPercentagesOfTheTotal()
    {
        Assert.IsNotNull(generatedReport, "Expected a generated report but it was null.");
        Assert.AreEqual(100, generatedReport!.TotalSales.Percentage, $"Expected total sales percentage to be 100% but got {generatedReport.TotalSales.Percentage}%");
        foreach (var pizzaReport in generatedReport.SalesByPizza)
        {
            Assert.IsTrue(pizzaReport.Value.Percentage is >= 0 and <= 100, $"Expected percentage for {pizzaReport.Key} to be between 0% and 100% but got {pizzaReport.Value.Percentage}%");
        }
        foreach (var dayReport in generatedReport.SalesByDay)
        {
            Assert.IsTrue(dayReport.Value.Percentage is >= 0 and <= 100, $"Expected percentage for {dayReport.Key} to be between 0% and 100% but got {dayReport.Value.Percentage}%");
        }
    }
}
