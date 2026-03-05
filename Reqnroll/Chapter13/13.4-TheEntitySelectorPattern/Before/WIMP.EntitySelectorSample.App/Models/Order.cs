namespace WIMP.EntitySelectorSample.App.Models;

public class Order(int orderNo, string customerName, string pizzaName, TimeSpan placingTime)
{
    public int OrderNo { get; set; } = orderNo;
    public string CustomerName { get; set; } = customerName;
    public string PizzaName { get; set; } = pizzaName;
    public TimeSpan PlacingTime { get; set; } = placingTime;
    public OrderStatus Status { get; set; } = OrderStatus.New;
}
