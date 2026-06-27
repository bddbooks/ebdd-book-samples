using WIMP.App.Models;
using WIMP.App.Services;

namespace WIMP.App.RestApi;

public static class RestApiMappings
{
    public static void Register(WebApplication app)
    {
        // ─── Authentication ───────────────────────────────────────────────────────────

        app.MapPost("/api/auth/register", (RegisterRequest request, AuthenticationService auth) =>
            {
                var result =
                    auth.Register(request.CustomerName ?? string.Empty, request.Email ?? string.Empty);
                return result.Successful
                    ? Results.Created($"/api/customers/{Uri.EscapeDataString(result.Value.Name)}", result.Value)
                    : Results.BadRequest(new ErrorResponse(result.ErrorMessage));
            })
            .WithName("Register")
            .WithSummary("Register a new customer account (name + email).");

        app.MapPost("/api/auth/login", (LoginRequest request, AuthenticationService auth) =>
            {
                var result =
                    auth.Login(request.CustomerName ?? string.Empty, request.Password ?? string.Empty);
                return result.Successful
                    ? Results.Ok(new LoginResponse(result.Value, request.CustomerName!))
                    : Results.Json(new ErrorResponse(result.ErrorMessage), statusCode: StatusCodes.Status401Unauthorized);
            })
            .WithName("Login")
            .WithSummary("Log in with customer name and password. Returns a Bearer token.");

        app.MapDelete("/api/auth/logout", (HttpContext ctx, AuthenticationService auth) =>
            {
                string? token = auth.ExtractToken(ctx.Request.Headers.Authorization);
                if (token is null)
                {
                    return Results.Unauthorized();
                }

                auth.Logout(token);
                return Results.NoContent();
            })
            .WithName("Logout")
            .WithSummary("Log out the currently authenticated customer.");

        // ─── Orders ──────────────────────────────────────────────────────────────────

        app.MapPost("/api/orders",
                (PlaceOrderRequest request, HttpContext ctx, AuthenticationService auth, OrderService orders) =>
                {
                    string? token = auth.ExtractToken(ctx.Request.Headers.Authorization);
                    string? customerName = auth.GetCustomerName(token);
                    if (customerName is null)
                    {
                        return Results.Json(new ErrorResponse("The request is not authorized for this operation"),
                            statusCode: StatusCodes.Status401Unauthorized);
                    }

                    var items = request.Items?.ToList() ?? [];
                    var result =
                        orders.PlaceOrder(customerName, request.CustomerEmail ?? string.Empty,
                            items, request.DeliveryAddress ?? string.Empty, request.PlacingTime, request.ExpectedDeliveryTime);
                    return result.Successful
                        ? result.Value.Status == OrderStatus.Rejected
                            ? Results.BadRequest(result.Value)
                            : Results.Created($"/api/orders/{result.Value.OrderNo}", result.Value)
                        : Results.BadRequest(result.ErrorMessage);
                })
            .WithName("PlaceOrder")
            .WithSummary("Place a new pizza order. Requires authentication.");

        app.MapGet("/api/orders/{orderNo:int}", (int orderNo, OrderService orders) =>
            {
                var order = orders.GetOrder(orderNo);
                return order is null ? Results.NotFound() : Results.Ok(order);
            })
            .WithName("GetOrder")
            .WithSummary("Retrieve an order by its order number.");

        app.MapDelete("/api/orders/{orderNo:int}",
                (int orderNo, HttpContext ctx, AuthenticationService auth, OrderService orders) =>
                {
                    string? token = auth.ExtractToken(ctx.Request.Headers.Authorization);
                    string? customerName = auth.GetCustomerName(token);
                    if (customerName is null)
                    {
                        return Results.Json(new ErrorResponse("The request is not authorized for this operation"),
                            statusCode: StatusCodes.Status401Unauthorized);
                    }

                    var result = orders.CancelOrder(orderNo, customerName);
                    return result.Successful ? Results.NoContent() : Results.BadRequest(new ErrorResponse(result.ErrorMessage));
                })
            .WithName("CancelOrder")
            .WithSummary("Cancel an order. Only the owning customer can cancel their order.");

        app.MapPost("/api/orders/{orderNo:int}/deliver", (int orderNo, OrderService orders) =>
            {
                var result = orders.DeliverOrder(orderNo);
                return result.Successful ? Results.Ok() : Results.NotFound(result.ErrorMessage);
            })
            .WithName("DeliverOrder")
            .WithSummary("Mark an order as delivered. Triggers coupon email if applicable.");

        app.MapPut("/api/orders/{orderNo:int}/delivery-address",
                (int orderNo, ChangeAddressRequest request, OrderService orders) =>
                {
                    if (string.IsNullOrWhiteSpace(request.Address))
                    {
                        return Results.BadRequest(new ErrorResponse("Address is required."));
                    }

                    var result = orders.ChangeDeliveryAddress(orderNo, request.Address);
                    return result.Successful ? Results.Ok() : Results.BadRequest(new ErrorResponse(result.ErrorMessage));
                })
            .WithName("ChangeDeliveryAddress")
            .WithSummary("Change the delivery address of an order (only allowed before pickup).");

        // ─── Kitchen ─────────────────────────────────────────────────────────────────

        app.MapPost("/api/kitchen/take-next-order", (OrderService orders) =>
            {
                var order = orders.TakeNextOrder();
                return order is null ? Results.NoContent() : Results.Ok(order);
            })
            .WithName("TakeNextOrder")
            .WithSummary("Kitchen staff takes the next order to work on (FIFO by placing time).");

        app.MapPost("/api/orders/{orderNo:int}/ready-for-pickup", (int orderNo, OrderService orders) =>
            {
                var result = orders.SetWaitingForPickup(orderNo);
                return result.Successful ? Results.Ok() : Results.BadRequest(new ErrorResponse(result.ErrorMessage));
            })
            .WithName("ReadyForPickup")
            .WithSummary("Mark an order as ready for pickup (must be in preparation first).");

        // ─── Promotions ───────────────────────────────────────────────────────────────

        app.MapPost("/api/promotions", (ActivatePromotionRequest request, PromotionService promotions) =>
            {
                if (string.IsNullOrWhiteSpace(request.Name))
                {
                    return Results.BadRequest(new ErrorResponse("Promotion name is required."));
                }

                var promotion = promotions.ActivatePromotion(request.Name);
                return Results.Created($"/api/promotions/{Uri.EscapeDataString(promotion.Name)}", promotion);
            })
            .WithName("ActivatePromotion")
            .WithSummary("Activate a named promotion.");

        // ─── Notifications ────────────────────────────────────────────────────────────

        app.MapGet("/api/notifications/{customerName}", (string customerName, NotificationService notifications) =>
                Results.Ok(notifications.GetNotifications(customerName)))
            .WithName("GetNotifications")
            .WithSummary("Get all notifications for a customer.");

        // ─── Coupons ──────────────────────────────────────────────────────────────────

        app.MapGet("/api/coupons/{customerEmail}", (string customerEmail, EmailService email) =>
                Results.Ok(email.GetCoupons(customerEmail)))
            .WithName("GetCoupons")
            .WithSummary("Get all coupons sent to a customer's email address.");
    }
}
