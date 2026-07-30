using System.Globalization;

using CsvHelper;

using DiffPlex.Renderer;

using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver,
    IFeatureContext featureContext)
{
    private SalesReport? generatedReport;

    [Given("sales traffic from {string}")]
    public async Task GivenSalesTrafficFrom(string fileName)
    {
        string csvFilePath = Path.Combine(
            featureContext.FeatureInfo.FolderPath, fileName);
        using var reader = new StreamReader(csvFilePath);
        using var csv = new CsvReader(reader, CultureInfo.InvariantCulture);
        var salesTraffic =
            csv.GetRecords<DailyPizzaSales>().ToArray();

        // Fail-fast check: if the CSV file is empty, we can fail because
        // that might be a sign of some environmental error.
        if (salesTraffic.Length == 0)
        {
            throw new InvalidOperationException($"The CSV file '{csvFilePath}' was empty!");
        }

        await backdoorApiDriver
            .PrepareSalesTraffic(salesTraffic)
            .Execute();
    }

    [Given("a week of operations with these pizza sales:")]
    public async Task GivenAWeekOfOperationsWithThesePizzaSales(DataTable salesTable)
    {
        await backdoorApiDriver.PrepareSalesTraffic(salesTable).Execute();
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
