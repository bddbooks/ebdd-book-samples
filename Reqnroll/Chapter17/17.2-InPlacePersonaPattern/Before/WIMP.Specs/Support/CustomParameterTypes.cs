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

    [StepArgumentTransformation(@"order (\d+)", Name = "order")]
    public int ConvertOrderNumber(int orderNo)
    {
        return orderNo;
    }
}
