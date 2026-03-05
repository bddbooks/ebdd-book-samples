namespace WIMP.IntroduceAndReferSample.App.Models;

public class Order(int orderNo, string customerName, string pizzaName)
{
    public int OrderNo { get; set; } = orderNo;
    public string CustomerName { get; set; } = customerName;
    public string PizzaName { get; set; } = pizzaName;
    public OrderStatus Status { get; set; } = OrderStatus.New;
}
