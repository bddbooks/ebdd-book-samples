using System.Globalization;

using Microsoft.Extensions.Logging;

using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class BackdoorApiDriver(ILoggerFactory loggerFactory, RestApiContext restApiContext)
{
    public TestAction<VoidReturn> PrepareSalesTraffic(IEnumerable<DailyPizzaSales> salesData)
    {
        var salesDataInput = salesData.ToArray();
        return new LambdaAction(loggerFactory, "Prepare sales traffic", salesDataInput, async () =>
            await restApiContext.ProcessRequest<VoidReturn>(
                "Prepare sales traffic", HttpMethod.Post, "/api/test/prepare-sales-traffic",
                salesDataInput));
    }

    public TestAction<VoidReturn> PrepareSalesTraffic(DataTable salesTable)
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

        return PrepareSalesTraffic(salesTraffic);
    }
}
