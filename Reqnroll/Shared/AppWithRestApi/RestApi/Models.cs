using WIMP.App.Models;

namespace WIMP.App.RestApi;

public record RegisterRequest(string? CustomerName, string? Email);
public record LoginRequest(string? CustomerName, string? Password);
public record LoginResponse(string Token, string CustomerName);
public record PlaceOrderRequest(PizzaItem[]? Items, string? DeliveryAddress, string? CustomerEmail, DateTimeOffset? PlacingTime = null, DateTimeOffset? ExpectedDeliveryTime = null);
public record ChangeAddressRequest(string Address);
public record ActivatePromotionRequest(string Name);
public record ErrorResponse(string Error);
