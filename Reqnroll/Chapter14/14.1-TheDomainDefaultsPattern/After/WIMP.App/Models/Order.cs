namespace WIMP.App.Models;

public class Order
{
    public int OrderNo { get; set; }
    public OrderStatus Status { get; set; } = OrderStatus.New;
    public DateTimeOffset PlacingTime { get; set; }
    public string CustomerName { get; set; } = null!;

    private readonly List<PizzaItem> items = new();
    public IReadOnlyList<PizzaItem> Items => items.AsReadOnly();

    public void AddItem(string pizzaName, string size)
    {
        items.Add(new PizzaItem(pizzaName, size));
    }
}
