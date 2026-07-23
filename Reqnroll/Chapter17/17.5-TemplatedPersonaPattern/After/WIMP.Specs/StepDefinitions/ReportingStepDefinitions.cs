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
    public class SalesFilterData
    {
        public required string ExcludeSection { get; set; }
    }

    private SalesReport? generatedReport;

    [Given("sales traffic from {string}")]
    public async Task GivenSalesTrafficFrom(string fileName)
    {
        await LoadTrafficData(fileName, []);
    }

    [Given("sales traffic from {string} with")]
    public async Task GivenSalesTrafficFrom(string fileName, DataTable filterTable)
    {
        await LoadTrafficData(fileName, filterTable.CreateSet<SalesFilterData>());
    }

    private async Task LoadTrafficData(string fileName, IEnumerable<SalesFilterData> filterData)
    {
        string csvFilePath = Path.Combine(
            featureContext.FeatureInfo.FolderPath, fileName);
        using var reader = new StreamReader(csvFilePath);
        using var csv = new CsvReader(reader, CultureInfo.InvariantCulture);
        var salesTraffic =
            csv.GetRecords<DailyPizzaSales>();

        if (filterData.Any(fd => fd.ExcludeSection == "truffle sales"))
        {
            salesTraffic = salesTraffic.Where(r => r.Pizza != "Truffle Bliss");
        }

        await backdoorApiDriver
            .PrepareSalesTraffic(salesTraffic)
            .Execute();
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


    [When("the ingredient usage report is requested for the week beginning {DateOnly}")]
    public void WhenTheIngredientUsageReportIsRequestedForTheWeekBeginning(DateOnly startDay)
    {
        // ingredient usage report generation is not part of this sample
    }

    [Then("the ingredient usage report should contain {string} usage as {int} portions")]
    public void ThenTheIngredientUsageReportShouldContainUsageAsPortions(string ingredient, int expectedPortions)
    {
        // ingredient usage report generation is not part of this sample
    }
}
