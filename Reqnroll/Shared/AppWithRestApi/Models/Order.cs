namespace WIMP.App.Models;

public class Order
{
    public int OrderNo { get; set; }
    public string CustomerName { get; set; } = string.Empty;
    public string CustomerEmail { get; set; } = string.Empty;
    public List<PizzaItem> Items { get; set; } = [];
    public string DeliveryAddress { get; set; } = string.Empty;
    public OrderStatus Status { get; set; } = OrderStatus.New;
    public string? StatusMessage { get; set; }
    public TimeSpan PlacingTime { get; set; }
}
