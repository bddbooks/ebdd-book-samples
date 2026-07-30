using Reqnroll;

namespace WIMP.Specs.Support;

[Binding]
public class CustomParameterTypes(OrderingContext orderingContext)
{
    [StepArgumentTransformation(@"(\d+-\d+-\d+)")]
    public DateOnly ConvertDateOnly(string value)
    {
        return DateOnly.Parse(value);
    }

    [StepArgumentTransformation("the order|the placed order", Name = "order")]
    public int ConvertOrder()
    {
        return orderingContext.CurrentOrderNo ??
            throw new InvalidOperationException("No current order");
    }

    [StepArgumentTransformation(@"order ([A-Z])", Name = "order")]
    public int ConvertNamedOrderNumber(string name)
    {
        return orderingContext.NamedOrders.TryGetValue(name, out int orderNo)
            ? orderNo
            : throw new InvalidOperationException($"Order {name} not known");
    }
}
