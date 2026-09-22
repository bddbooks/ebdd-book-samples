using Reqnroll;

using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class NotificationsStepDefinitions(NotificationsApiDriver notificationsApiDriver)
{
    [Then("the customer should receive a notification about the delay")]
    public async Task ThenTheCustomerShouldReceiveANotificationAboutTheDelay()
    {
        var notifications = await notificationsApiDriver.GetNotifications(DomainDefaults.CustomerName).Execute();

        Assert.IsNotNull(notifications);
        Assert.IsTrue(notifications.Any(n =>
                n.Message.Contains("delayed", StringComparison.OrdinalIgnoreCase)),
            "Expected a delay notification but none was found.");
    }
}
