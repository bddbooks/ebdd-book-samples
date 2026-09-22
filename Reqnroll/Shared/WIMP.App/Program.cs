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

using WIMP.App.Data;
using WIMP.App.RestApi;
using WIMP.App.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddTransient<AuthenticationService>();
builder.Services.AddTransient<PromotionService>();
builder.Services.AddTransient<NotificationService>();
builder.Services.AddTransient<EmailService>();
builder.Services.AddTransient<OrderService>();
builder.Services.AddTransient<PaymentService>();
builder.Services.AddTransient<ReportingService>();
builder.Services.AddTransient<MenuService>();
builder.Services.AddTransient<MessageService>();
builder.Services.AddTransient<MarketService>();
builder.Services.AddTransient<PricingService>();
builder.Services.AddSingleton<ITimeService, TimeService>();
builder.Services.AddTransient<IPaymentGateway, RealPaymentGateway>();

#if REALDB
// uses real database
builder.Services.AddDbContextFactory<WIMP.App.Data.Db.WimpDbContext>(
    options => WIMP.App.Data.Db.WimpDbContextOptionsBuilder.ConfigureDbContextOptions(options));
builder.Services.AddTransient<IDataRepository, WIMP.App.Data.Db.DataRepository>();
#else
// configuring application for stub data access for the sake of demonstration
builder.Services.AddSingleton<StubDataStore>();
builder.Services.AddTransient<IDataRepository, StubDataRepository>();
#endif

#if DEBUG
// test api
builder.Services.AddTransient<TestDataService>();
#endif

var app = builder.Build();

RestApiMappings.Register(app);

app.Run();

//TODO: is this really needed?
namespace WIMP.App
{
    // Required so WebApplicationFactory<Program> can reference the entry-point type from the test project.
    public partial class Program { }
}
