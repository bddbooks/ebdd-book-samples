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
using WIMP.App.Services;

namespace WIMP.App.RestApi;

public static class RestApiMappings
{
    public static void Register(WebApplication app)
    {
        // ─── Authentication ───────────────────────────────────────────────────────────

        app.MapPost("/api/auth/register", (RegisterRequest request, AuthenticationService authenticationService) =>
            {
                var result =
                    authenticationService.Register(request.CustomerName ?? string.Empty, request.Email ?? string.Empty);
                return result.Successful
                    ? Results.Created($"/api/customers/{Uri.EscapeDataString(result.Value.Name)}", result.Value)
                    : Results.BadRequest(new ErrorResponse(result.ErrorMessage));
            })
            .WithName("Register")
            .WithSummary("Register a new customer account (name + email).");

        app.MapPost("/api/auth/login", (LoginRequest request, AuthenticationService authenticationService) =>
            {
                var result =
                    authenticationService.Login(request.CustomerName ?? string.Empty, request.Password ?? string.Empty);
                return result.Successful
                    ? Results.Ok(new LoginResponse(result.Value, request.CustomerName!))
                    : Results.Json(new ErrorResponse(result.ErrorMessage), statusCode: StatusCodes.Status401Unauthorized);
            })
            .WithName("Login")
            .WithSummary("Log in with customer name and password. Returns a Bearer token.");

        app.MapDelete("/api/auth/logout", (HttpContext ctx, AuthenticationService authenticationService) =>
            {
                string? token = authenticationService.ExtractToken(ctx.Request.Headers.Authorization);
                if (token is null)
                {
                    return Results.Unauthorized();
                }

                authenticationService.Logout(token);
                return Results.NoContent();
            })
            .WithName("Logout")
            .WithSummary("Log out the currently authenticated customer.");

        // ─── Orders ──────────────────────────────────────────────────────────────────

        app.MapPost("/api/orders",
                (PlaceOrderRequest request, HttpContext ctx, AuthenticationService authenticationService, OrderService orderService) =>
                {
                    string? token = authenticationService.ExtractToken(ctx.Request.Headers.Authorization);
                    string? customerName = authenticationService.GetCustomerName(token);
                    if (customerName is null)
                    {
                        return Results.Json(new ErrorResponse("The request is not authorized for this operation"),
                            statusCode: StatusCodes.Status401Unauthorized);
                    }

                    var items = request.Items?.ToList() ?? [];
                    var result =
                        orderService.PlaceOrder(customerName, request.CustomerEmail ?? string.Empty,
                            items, request.DeliveryAddress ?? string.Empty, request.PlacingTime, request.ExpectedDeliveryTime, request.DisableMenuIntegrityCheck);
                    return result.Successful
                        ? Results.Created($"/api/orders/{result.Value.OrderNo}", result.Value)
                        : Results.BadRequest(result.ErrorMessage);
                })
            .WithName("PlaceOrder")
            .WithSummary("Place a new pizza order. Requires authentication.");

        app.MapGet("/api/orders/{orderNo:int}", (int orderNo, OrderService orderService) =>
            {
                var order = orderService.GetOrder(orderNo);
                return order is null ? Results.NotFound() : Results.Ok(order);
            })
            .WithName("GetOrder")
            .WithSummary("Retrieve an order by its order number.");

        app.MapDelete("/api/orders/{orderNo:int}",
                (int orderNo, HttpContext ctx, AuthenticationService authenticationService, OrderService orderService) =>
                {
                    string? token = authenticationService.ExtractToken(ctx.Request.Headers.Authorization);
                    string? customerName = authenticationService.GetCustomerName(token);
                    if (customerName is null)
                    {
                        return Results.Json(new ErrorResponse("The request is not authorized for this operation"),
                            statusCode: StatusCodes.Status401Unauthorized);
                    }

                    var result = orderService.CancelOrder(orderNo, customerName);
                    return result.Successful ? Results.NoContent() : Results.BadRequest(new ErrorResponse(result.ErrorMessage));
                })
            .WithName("CancelOrder")
            .WithSummary("Cancel an order. Only the owning customer can cancel their order.");

        app.MapPost("/api/orders/{orderNo:int}/deliver", (int orderNo, OrderService orderService) =>
            {
                var result = orderService.DeliverOrder(orderNo);
                return result.Successful ? Results.Ok(result.Value) : Results.NotFound(result.ErrorMessage);
            })
            .WithName("DeliverOrder")
            .WithSummary("Mark an order as delivered. Triggers coupon email if applicable.");

        app.MapPut("/api/orders/{orderNo:int}/delivery-address",
                (int orderNo, ChangeAddressRequest request, OrderService orderService) =>
                {
                    if (string.IsNullOrWhiteSpace(request.Address))
                    {
                        return Results.BadRequest(new ErrorResponse("Address is required."));
                    }

                    var result = orderService.ChangeDeliveryAddress(orderNo, request.Address);
                    return result.Successful ? Results.Ok() : Results.BadRequest(new ErrorResponse(result.ErrorMessage));
                })
            .WithName("ChangeDeliveryAddress")
            .WithSummary("Change the delivery address of an order (only allowed before pickup).");

        app.MapPut("/api/orders/{orderNo:int}/customer-collection",
                (int orderNo, HttpContext ctx, AuthenticationService authenticationService, OrderService orderService) =>
                {
                    string? token = authenticationService.ExtractToken(ctx.Request.Headers.Authorization);
                    string? customerName = authenticationService.GetCustomerName(token);
                    if (customerName is null)
                    {
                        return Results.Json(new ErrorResponse("The request is not authorized for this operation"),
                            statusCode: StatusCodes.Status401Unauthorized);
                    }

                    var result = orderService.SetForCustomerCollection(orderNo, customerName);
                    return result.Successful ? Results.Ok(result.Value.OrderCollectionDetails) : Results.BadRequest(new ErrorResponse(result.ErrorMessage));
                })
            .WithName("SetForCustomerCollection")
            .WithSummary("Change the delivery method of an order to customer collection.");

        app.MapPut("/api/orders/{orderNo:int}/contact-details",
                (int orderNo, ContactDetails contactDetails, HttpContext ctx, AuthenticationService authenticationService, OrderService orderService) =>
                {
                    string? token = authenticationService.ExtractToken(ctx.Request.Headers.Authorization);
                    string? customerName = authenticationService.GetCustomerName(token);
                    if (customerName is null)
                    {
                        return Results.Json(new ErrorResponse("The request is not authorized for this operation"),
                            statusCode: StatusCodes.Status401Unauthorized);
                    }

                    var result = orderService.ProvideContactDetails(orderNo, contactDetails);
                    return result.Successful ? Results.Ok(result.Value) : Results.BadRequest(new ErrorResponse(result.ErrorMessage));
                })
            .WithName("ProvideContactDetails")
            .WithSummary("Provide contact details for an order.");

        // ─── Payment ──────────────────────────────────────────────────────────────────

        app.MapPost("/api/payment/authorize", (int orderNo, PaymentService paymentService) =>
            {
                var result = paymentService.AuthorizePaymentFor(orderNo);
                return result.Successful ? Results.Ok(result.Value) : Results.BadRequest(new ErrorResponse(result.ErrorMessage));
            })
            .WithName("AuthorizePayment")
            .WithSummary("Authorize the payment for an order.");

        // ─── Kitchen ─────────────────────────────────────────────────────────────────

        app.MapPost("/api/kitchen/take-next-order", (HttpContext ctx, AuthenticationService authenticationService, OrderService orderService) =>
            {
                string? token = authenticationService.ExtractToken(ctx.Request.Headers.Authorization);
                string? customerName = authenticationService.GetCustomerName(token);
                if (customerName is null || !customerName.StartsWith("Kitchen"))
                {
                    return Results.Json(new ErrorResponse("The request is not authorized for this operation"),
                        statusCode: StatusCodes.Status401Unauthorized);
                }

                var order = orderService.TakeNextOrder();
                return order is null ? Results.NoContent() : Results.Ok(order);
            })
            .WithName("TakeNextOrder")
            .WithSummary("Kitchen staff takes the next order to work on (FIFO by placing time).");

        app.MapPost("/api/kitchen/ready-for-pickup/{orderNo:int}", (int orderNo, OrderService orderService) =>
            {
                var result = orderService.SetWaitingForPickup(orderNo);
                return result.Successful ? Results.Ok() : Results.BadRequest(new ErrorResponse(result.ErrorMessage));
            })
            .WithName("ReadyForPickup")
            .WithSummary("Mark an order as ready for pickup (must be in preparation first).");

        // ─── Promotions ───────────────────────────────────────────────────────────────

        app.MapPost("/api/promotions", (ActivatePromotionRequest request, PromotionService promotionService) =>
            {
                if (string.IsNullOrWhiteSpace(request.Name))
                {
                    return Results.BadRequest(new ErrorResponse("Promotion name is required."));
                }

                var promotion = promotionService.ActivatePromotion(request.Name);
                return Results.Created($"/api/promotions/{Uri.EscapeDataString(promotion.Name)}", promotion);
            })
            .WithName("ActivatePromotion")
            .WithSummary("Activate a named promotion.");

        // ─── Coupons ──────────────────────────────────────────────────────────────────

        app.MapGet("/api/coupons/{customerEmail}", (string customerEmail, EmailService emailService) =>
                Results.Ok(emailService.GetCoupons(customerEmail)))
            .WithName("GetCoupons")
            .WithSummary("Get all coupons sent to a customer's email address.");

        // ─── Notifications ────────────────────────────────────────────────────────────

        app.MapGet("/api/notifications/{customerName}", (string customerName, NotificationService notificationService) =>
                Results.Ok(notificationService.GetNotifications(customerName)))
            .WithName("GetNotifications")
            .WithSummary("Get all notifications for a customer.");

        // ─── Reporting ──────────────────────────────────────────────────────────────────

        app.MapPost("/api/reporting/sales-report", (DateOnly startDay, HttpContext ctx, AuthenticationService authenticationService, ReportingService reportingService) =>
            {
                string? token = authenticationService.ExtractToken(ctx.Request.Headers.Authorization);
                string? customerName = authenticationService.GetCustomerName(token);
                if (customerName is null || !customerName.StartsWith("Owner"))
                {
                    return Results.Json(new ErrorResponse("The request is not authorized for this operation"),
                        statusCode: StatusCodes.Status401Unauthorized);
                }

                var salesReport = reportingService.GenerateSalesReport(startDay);
                return Results.Ok(salesReport);
            })
            .WithName("GenerateSalesReport")
            .WithSummary("Generates sales report data for restaurant owners");

        // ─── Menu ──────────────────────────────────────────────────────────────────

        app.MapGet("/api/menu", (string? promo, decimal? minPrice, decimal? maxPrice, int? maxCalories, MenuService menuService) =>
            {
                var filter = minPrice is null && maxPrice is null && maxCalories is null
                    ? null
                    : new MenuFilter { MinPrice = minPrice, MaxPrice = maxPrice, MaxCalories = maxCalories };
                return Results.Ok(menuService.LoadMenu(promo ?? "", filter));
            })
            .WithName("GetMenu")
            .WithSummary("Get menu items to be displayed.");

        app.MapPost("/api/menu",
                (MenuItem menuItem, HttpContext ctx, AuthenticationService authenticationService, MenuService menuService) =>
                {
                    string? token = authenticationService.ExtractToken(ctx.Request.Headers.Authorization);
                    string? customerName = authenticationService.GetCustomerName(token);
                    if (customerName is null || !customerName.StartsWith("Owner"))
                    {
                        return Results.Json(new ErrorResponse("The request is not authorized for this operation"),
                            statusCode: StatusCodes.Status401Unauthorized);
                    }

                    var result =
                        menuService.AddMenuItem(menuItem);
                    return result.Successful
                        ? Results.Created()
                        : Results.BadRequest(result.ErrorMessage);
                })
            .WithName("AddMenuItem")
            .WithSummary("Registers a menu item. For restaurant owners");

#if DEBUG
        // ─── TEST API ──────────────────────────────────────────────────────────────────

        app.MapPost("/api/test/prepare-order",
                (OrderStatus status, string customerName, PlaceOrderRequest request, TestDataService testDataService) =>
                {
                    var result = testDataService.PrepareTestOrder(customerName, request.CustomerEmail ?? string.Empty,
                            request.Items?.ToList() ?? [], request.DeliveryAddress ?? string.Empty, status, request.PlacingTime, request.ExpectedDeliveryTime);
                    return result.Successful
                        ? Results.Ok(result.Value)
                        : Results.BadRequest(result.ErrorMessage);
                })
            .WithName("PrepareTestOrder")
            .WithSummary("Prepares a test order");

        app.MapPost("/api/test/prepare-sales-traffic",
                (DailyPizzaSales[] salesEntries, TestDataService testDataService) =>
                {
                    var result = testDataService.PrepareSalesTraffic(salesEntries);
                    return result.Successful
                        ? Results.Ok()
                        : Results.BadRequest(result.ErrorMessage);
                })
            .WithName("PrepareSalesTraffic")
            .WithSummary("Prepares sales traffic data");
#endif
    }
}
