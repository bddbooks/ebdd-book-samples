using DiffPlex.Renderer;

using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver)
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
}
