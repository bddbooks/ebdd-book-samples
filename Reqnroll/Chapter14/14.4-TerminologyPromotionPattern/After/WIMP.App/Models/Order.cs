/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

namespace WIMP.App.Models;

public class Order
{
    public int OrderNo { get; set; }
    public OrderStatus Status { get; set; } = OrderStatus.New;
    public DateTimeOffset PlacingTime { get; set; }
    public string CustomerName { get; set; } = null!;
    public string? DeliveryAddress { get; set; }

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
