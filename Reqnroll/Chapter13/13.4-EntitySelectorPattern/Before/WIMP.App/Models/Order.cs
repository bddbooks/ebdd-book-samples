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

public class Order(int orderNo, string customerName, string pizzaName, TimeSpan placingTime)
{
    public int OrderNo { get; set; } = orderNo;
    public string CustomerName { get; set; } = customerName;
    public string PizzaName { get; set; } = pizzaName;
    public TimeSpan PlacingTime { get; set; } = placingTime;
    public OrderStatus Status { get; set; } = OrderStatus.New;
}
