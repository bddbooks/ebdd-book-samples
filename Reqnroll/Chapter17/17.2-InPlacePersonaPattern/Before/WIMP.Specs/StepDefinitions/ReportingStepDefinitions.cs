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
