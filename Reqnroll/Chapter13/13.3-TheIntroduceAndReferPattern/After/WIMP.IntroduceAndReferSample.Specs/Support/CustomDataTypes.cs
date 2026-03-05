using Reqnroll;

using WIMP.IntroduceAndReferSample.App.Services;

namespace WIMP.IntroduceAndReferSample.Specs.Support;

[Binding]
public class CustomDataTypes(OrderingContext orderingContext)
{
    [StepArgumentTransformation("the order|the placed order", Name = "order")]
    public int ConvertOrder()
    {
        return orderingContext.PlacedOrderNo ??
            throw new InvalidOperationException("Order not chosen");
    }

    [StepArgumentTransformation(@"the order #(\d+)", Name = "order")]
    public int ConvertOrderNumber(int orderNo)
    {
        var order = OrderService.GetOrder(orderNo);
        return order?.OrderNo ??
            throw new InvalidOperationException("Order not found");
    }
}
