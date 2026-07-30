using Microsoft.Extensions.Configuration;

namespace WIMP.Specs.Support;

public class TestConfigurationProvider
{
    public class DatabaseConfiguration
    {
        public bool UseStub { get; set; } = true;
        public string ConnectionString { get; set; } = null!;
    }

    public class PaymentGatewayConfiguration
    {
        public string Url { get; set; } = null!;
        public string ApiKey { get; set; } = null!;
    }


    private readonly IConfigurationRoot configurationRoot =
        new ConfigurationBuilder()
            .SetBasePath(AppContext.BaseDirectory)
            .AddJsonFile("testconfig.json", optional: true)
            .AddEnvironmentVariables(prefix: "WIMP__")
            .Build();


    public PaymentGatewayConfiguration PaymentGateway
    {
        get
        {
            var paymentGatewayConfig = configurationRoot
                .GetRequiredSection("PaymentGateway")
                .Get<PaymentGatewayConfiguration>()!;
            return paymentGatewayConfig.Url is null
                ? throw new InvalidOperationException("Missing configuration setting: PaymentGateway:Url")
                : paymentGatewayConfig.ApiKey is null
                    ? throw new InvalidOperationException("Missing configuration setting: PaymentGateway:ApiKey")
                    : paymentGatewayConfig;
        }
    }

    public DatabaseConfiguration Database
    {
        get
        {
            var databaseConfig = configurationRoot
                .GetRequiredSection("Database")
                .Get<DatabaseConfiguration>()!;
            return databaseConfig.ConnectionString is null
                ? throw new InvalidOperationException("Missing configuration setting: Database:ConnectionString")
                : databaseConfig;
        }
    }
}

