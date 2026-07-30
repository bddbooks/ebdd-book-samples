using Reqnroll;

namespace WIMP.Specs.Support;

[Binding]
public class CustomParameterTypes(OrderingContext orderingContext)
{
    [StepArgumentTransformation("the earliest order received", Name = "order")]
    public int ConvertEarliestOrder()
    {
        return orderingContext.PlacedOrders
            .OrderBy(o => o.PlacingTime)
            .FirstOrDefault()?.OrderNo ??
            throw new InvalidOperationException("No orders available");
    }
}
