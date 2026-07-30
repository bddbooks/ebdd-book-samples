using Reqnroll;

namespace WIMP.Specs.Support;

[Binding]
public class CustomParameterTypes(OrderingContext orderingContext)
{
    [StepArgumentTransformation("the order|the placed order|the new order", Name = "order")]
    public int ConvertOrder()
    {
        return orderingContext.CurrentOrderNo ??
            throw new InvalidOperationException("No current order");
    }
}
