using WIMP.App.Models;
using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class BackdoorApiDriver(RestApiContext restApiContext)
{
    public TestAction<VoidReturn> PrepareSalesTraffic(IEnumerable<DailyPizzaSales> salesData) =>
        new LambdaAction("Prepare sales traffic", async () =>
            await restApiContext.ProcessRequest<VoidReturn>(
                "Prepare sales traffic", HttpMethod.Post, "/api/test/prepare-sales-traffic",
                salesData.ToArray()));
}
