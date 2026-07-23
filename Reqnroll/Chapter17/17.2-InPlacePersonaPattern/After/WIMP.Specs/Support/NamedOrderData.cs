using WIMP.App.Models;

namespace WIMP.Specs.Support;

public class NamedOrderData
{
    public string OrderName { get; set; } = string.Empty;
    public OrderStatus Status { get; set; }
}
