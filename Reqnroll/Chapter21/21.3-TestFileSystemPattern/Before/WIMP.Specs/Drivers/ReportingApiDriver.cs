using Microsoft.Extensions.Logging;

using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class ReportingApiDriver(ILoggerFactory loggerFactory, RestApiContext restApiContext)
{
    public TestAction<SalesReport> GenerateSalesReport(DateOnly startDay) =>
        new LambdaAction<SalesReport>(loggerFactory, "Generate sales report", startDay, async () =>
            await restApiContext.ProcessRequest<SalesReport>(
                "Generate sales report", HttpMethod.Post, $"/api/reporting/sales-report?startDay={startDay:O}"));
}
