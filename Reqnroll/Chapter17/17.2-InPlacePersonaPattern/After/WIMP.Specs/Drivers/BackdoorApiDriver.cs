using System.Globalization;

using Reqnroll;

using WIMP.App.Models;
using WIMP.App.RestApi;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class BackdoorApiDriver(RestApiContext restApiContext)
{
    public TestAction<Order> PrepareOrder(string customerName, PlaceOrderRequest orderRequest, OrderStatus status) =>
        new LambdaAction<Order>("Prepare test order", async () =>
            await restApiContext.ProcessRequest<Order>(
                "Prepare test order", HttpMethod.Post, $"/api/test/prepare-order?customerName={customerName}&status={status}",
                orderRequest));

    public TestAction<VoidReturn> PrepareSalesTraffic(IEnumerable<DailyPizzaSales> salesData) =>
        new LambdaAction("Prepare sales traffic", async () =>
            await restApiContext.ProcessRequest<VoidReturn>(
                "Prepare sales traffic", HttpMethod.Post, "/api/test/prepare-sales-traffic",
                salesData.ToArray()));

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
