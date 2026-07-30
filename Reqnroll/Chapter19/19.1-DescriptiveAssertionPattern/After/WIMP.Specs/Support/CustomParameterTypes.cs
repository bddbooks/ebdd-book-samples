using Reqnroll;

namespace WIMP.Specs.Support;

[Binding]
public class CustomParameterTypes
{
    [StepArgumentTransformation]
    public OrderData[] ConvertOrderData(DataTable orderDataTable)
    {
        return orderDataTable.CreateSet<OrderData>().ToArray();
    }
}
