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

using WIMP.App.Models;

namespace WIMP.App.Data.Db.DbRecords;

public sealed class OrderDbRecord
{
    public int OrderNo { get; set; }
    public required string CustomerName { get; set; }
    public required string CustomerEmail { get; set; }
    public required string ItemsJson { get; set; }
    public required string DeliveryAddress { get; set; }
    public OrderStatus Status { get; set; }
    public string? StatusMessage { get; set; }
    public TimeSpan PlacingTime { get; set; }
    public decimal Price { get; set; }
    public DeliveryMethod DeliveryMethod { get; set; }
    public string? CollectionDetailsJson { get; set; }
}
