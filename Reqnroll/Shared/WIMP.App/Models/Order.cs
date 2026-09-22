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
    public string CustomerName { get; set; } = string.Empty;
    public string CustomerEmail { get; set; } = string.Empty;
    public List<PizzaItem> Items { get; set; } = [];
    public string DeliveryAddress { get; set; } = string.Empty;
    public OrderStatus Status { get; set; } = OrderStatus.New;
    public string? StatusMessage { get; set; }
    public TimeSpan PlacingTime { get; set; }
    public decimal Price { get; set; }
    public DeliveryMethod DeliveryMethod { get; set; } = DeliveryMethod.DeliveryToAddress;
    public OrderCollectionDetails? OrderCollectionDetails { get; set; }
    public ContactDetails? ContactDetails { get; set; }
}
