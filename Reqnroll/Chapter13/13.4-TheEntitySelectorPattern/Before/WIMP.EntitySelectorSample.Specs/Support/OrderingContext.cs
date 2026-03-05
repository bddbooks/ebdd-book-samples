using WIMP.EntitySelectorSample.App.Models;

namespace WIMP.EntitySelectorSample.Specs.Support;

/// <summary>
/// Context class for sharing order-related data between step definition classes.
/// </summary>
public class OrderingContext
{
    public List<Order> PlacedOrders { get; set; } = [];
    public int? TakenOrderNo { get; set; }
}
