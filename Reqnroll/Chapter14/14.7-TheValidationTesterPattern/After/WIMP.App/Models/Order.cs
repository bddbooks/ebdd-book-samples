namespace WIMP.App.Models;

public class Order
{
    public int OrderNo { get; set; }
    public OrderStatus Status { get; set; } = OrderStatus.New;
    public DateTimeOffset PlacingTime { get; set; }
    public string CustomerName { get; set; } = null!;
    public OrderCollection Collection { get; set; }
    public ContactDetails? ContactDetails { get; set; }

    private readonly List<PizzaItem> items = new();
    public IReadOnlyList<PizzaItem> Items => items.AsReadOnly();

    public void AddItem(PizzaItem pizzaItem)
    {
        items.Add(pizzaItem);
    }

    public void ClearItems()
    {
        items.Clear();
    }
}
