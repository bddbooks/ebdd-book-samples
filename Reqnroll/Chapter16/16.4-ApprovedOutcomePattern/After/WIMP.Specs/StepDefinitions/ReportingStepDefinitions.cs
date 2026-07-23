using System.Globalization;

using DiffPlex.Renderer;

using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

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

    [Then("the report should show:")]
    public void ThenTheReportShouldShow(string expectedReport)
    {
        string actualReport = ReportPrinter.PrintReport(generatedReport ?? throw new InvalidOperationException("Report is not generated."));
        string diff = UnidiffRenderer.GenerateUnidiff(
            expectedReport, actualReport, "expected", "actual");
        Assert.IsTrue(string.IsNullOrEmpty(diff),
            "The report is different from the expected" + Environment.NewLine + diff);
    }
}
