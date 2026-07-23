using Reqnroll;

using WIMP.App.Services;

namespace WIMP.Specs.Support;

[Binding]
public class CustomParameterTypes
{
    [StepArgumentTransformation(@"the order #(\d+)", Name = "order")]
    public int ConvertOrderNumber(int orderNo)
    {
        var order = OrderService.GetOrder(orderNo);
        return order?.OrderNo ??
               throw new InvalidOperationException("Order not found");
    }
}
