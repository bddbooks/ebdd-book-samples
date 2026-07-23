namespace WIMP.Specs.Support;

public class OrderingContext
{
    public int? PlacedOrderNo { get; set; }
    public Dictionary<string, int> NamedOrders { get; } = new();
}
