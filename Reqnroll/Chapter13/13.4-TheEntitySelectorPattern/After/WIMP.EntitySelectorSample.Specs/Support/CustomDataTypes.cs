using Reqnroll;

using WIMP.EntitySelectorSample.App.Services;

namespace WIMP.EntitySelectorSample.Specs.Support;

[Binding]
public class CustomDataTypes(OrderingContext orderingContext)
{
    [StepArgumentTransformation(@"the order #(\d+)", Name = "order")]
    public int ConvertOrderNumber(int orderNo)
    {
        var order = OrderService.GetOrder(orderNo);
        return order?.OrderNo ??
               throw new InvalidOperationException("Order not found");
    }

    [StepArgumentTransformation("the earliest order received", Name = "order")]
    public int ConvertEarliestOrder()
    {
        return orderingContext.PlacedOrders
            .OrderBy(o => o.PlacingTime)
            .FirstOrDefault()?.OrderNo ??
            throw new InvalidOperationException("No orders available");
    }
}
