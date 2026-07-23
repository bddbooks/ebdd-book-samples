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
    private readonly Dictionary<string, DataTable> salesTrafficSamples = new();
    private SalesReport? generatedReport;

    [Given("the {string} weekly sales traffic is")]
    public void GivenTheWeeklySalesTrafficIs(string name, DataTable salesTraffic)
    {
        salesTrafficSamples[name] = salesTraffic;
    }

    [Given("the {string} weekly sales traffic")]
    public async Task GivenTheWeeklySalesTraffic(string trafficSampleName)
    {
        if (!salesTrafficSamples.TryGetValue(trafficSampleName, out var salesTraffic))
        {
            throw new InvalidOperationException($"Traffic {trafficSampleName} not known");
        }

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

    [When("the ingredient usage report is requested for the week beginning {DateOnly}")]
    public void WhenTheIngredientUsageReportIsRequestedForTheWeekBeginning(DateOnly startDay)
    {
        // ingredient usage report generation is not part of this sample 
    }

    [Then("the ingredient usage report should show:")]
    public void ThenTheIngredientUsageReportShouldShow(string expectedReport)
    {
        // ingredient usage report generation is not part of this sample
    }
}
