using Reqnroll;

using WIMP.EntitySelectorSample.App.Services;

namespace WIMP.EntitySelectorSample.Specs.Support;

[Binding]
public class CustomDataTypes
{
    [StepArgumentTransformation(@"the order #(\d+)", Name = "order")]
    public int ConvertOrderNumber(int orderNo)
    {
        var order = OrderService.GetOrder(orderNo);
        return order?.OrderNo ??
               throw new InvalidOperationException("Order not found");
    }
}
