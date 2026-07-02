using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.DependencyInjection.Extensions;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Debug;

using WIMP.App.Services;

namespace WIMP.Specs.Support;

public class WimpAppHost(StubTimeService timeService) : WebApplicationFactory<Program>
{
    protected override void ConfigureWebHost(IWebHostBuilder builder)
    {
        base.ConfigureWebHost(builder);
        builder.ConfigureLogging((_, loggingBuilder) =>
        {
            loggingBuilder.SetMinimumLevel(LogLevel.Debug);
            loggingBuilder.ClearProviders();
            loggingBuilder.Services.TryAddEnumerable(ServiceDescriptor.Singleton<ILoggerProvider, DebugLoggerProvider>(_ => new DebugLoggerProvider()));
        });
        builder.ConfigureServices(diConfig =>
            diConfig.AddSingleton<ITimeService>(timeService));
    }
}
