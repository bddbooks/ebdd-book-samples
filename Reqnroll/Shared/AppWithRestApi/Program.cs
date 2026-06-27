using WIMP.App.Data;
using WIMP.App.RestApi;
using WIMP.App.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddTransient<AuthenticationService>();
builder.Services.AddTransient<PromotionService>();
builder.Services.AddTransient<NotificationService>();
builder.Services.AddTransient<EmailService>();
builder.Services.AddTransient<OrderService>();
builder.Services.AddSingleton<ITimeService, TimeService>();

// configuring application for stub data access for the sake of demonstration
builder.Services.AddSingleton<StubDataStore>();
builder.Services.AddTransient<IDataRepository, StubDataRepository>();

var app = builder.Build();

RestApiMappings.Register(app);

app.Run();

//TODO: is this really needed?
namespace WIMP.App
{
    // Required so WebApplicationFactory<Program> can reference the entry-point type from the test project.
    public partial class Program { }
}
