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

namespace WIMP.App.RestApi;

public record RegisterRequest(string? CustomerName, string? Email);
public record LoginRequest(string? CustomerName, string? Password);
public record LoginResponse(string Token, string CustomerName);
public record PlaceOrderRequest(PizzaItem[]? Items, string? DeliveryAddress, string? CustomerEmail, DateTimeOffset? PlacingTime = null, DateTimeOffset? ExpectedDeliveryTime = null, bool DisableMenuIntegrityCheck = true); // the DisableMenuIntegrityCheck is true by default for the sake of demonstration
public record ChangeAddressRequest(string Address);
public record ActivatePromotionRequest(string Name);
public record ErrorResponse(string Error);
