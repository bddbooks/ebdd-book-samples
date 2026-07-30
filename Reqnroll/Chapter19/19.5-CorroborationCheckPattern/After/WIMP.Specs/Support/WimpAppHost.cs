using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.DependencyInjection.Extensions;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Debug;

using WIMP.Specs.Support.Logging;

namespace WIMP.Specs.Support;

public class WimpAppHost(AppLogContext appLogContext, ReqnrollLoggerProvider reqnrollLoggerProvider) : WebApplicationFactory<Program>
{
    protected override void ConfigureWebHost(IWebHostBuilder builder)
    {
        base.ConfigureWebHost(builder);
        builder.ConfigureLogging((_, loggingBuilder) =>
        {
            loggingBuilder.SetMinimumLevel(LogLevel.Debug);
            loggingBuilder.ClearProviders();
            // log app log entries to debug console
            loggingBuilder.Services.TryAddEnumerable(ServiceDescriptor.Singleton<ILoggerProvider, DebugLoggerProvider>(_ => new DebugLoggerProvider()));
            // log app log entries to AppLogContext
            loggingBuilder.Services.TryAddEnumerable(ServiceDescriptor.Singleton<ILoggerProvider, AppHostLoggerProvider>(_ => new AppHostLoggerProvider(appLogContext)));
            // embed app log to the test log
            loggingBuilder.Services.TryAddEnumerable(ServiceDescriptor.Singleton<ILoggerProvider, EmbeddedAppHostLoggerProvider>(_ => new EmbeddedAppHostLoggerProvider(reqnrollLoggerProvider)));
        });
    }
}
