namespace WIMP.LocalScenarioContextSample.App.Models;

public class Order(int orderNo, string customerName, string pizzaName)
{
    public int OrderNo { get; } = orderNo;
    public string CustomerName { get; } = customerName;
    public string PizzaName { get; } = pizzaName;
    public OrderStatus Status { get; set; } = OrderStatus.New;
}
