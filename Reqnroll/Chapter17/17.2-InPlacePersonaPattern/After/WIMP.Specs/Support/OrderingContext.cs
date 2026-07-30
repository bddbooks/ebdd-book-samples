namespace WIMP.Specs.Support;

public class OrderingContext
{
    public int? CurrentOrderNo { get; set; }
    public Dictionary<string, int> NamedOrders { get; } = new();
}
