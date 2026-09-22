using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Abstractions;

using Reqnroll;
using Reqnroll.BoDi;

using WIMP.App.Data;
using WIMP.App.Services;
using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

[Binding]
public class DiConfiguration
{
    [BeforeScenario(Order = 0)]
    public void SetupDependencies(IObjectContainer scenarioContainer)
    {
        if (Environment.GetEnvironmentVariable("WIMP_TEST_TARGET") == "rest")
        {
            scenarioContainer.RegisterTypeAs<AuthenticationApiDriver, IAuthenticationDriver>();
        }
        else
        {
            scenarioContainer.RegisterTypeAs<AuthenticationServiceDriver, IAuthenticationDriver>();

            // Additional DI registrations to allow injecting AuthenticationService to AuthenticationServiceDriver
            scenarioContainer.RegisterTypeAs<StubDataRepository, IDataRepository>();
            scenarioContainer.RegisterInstanceAs<ILogger<AuthenticationService>>(NullLogger<AuthenticationService>.Instance);
        }
    }
}
