using Reqnroll;

using WIMP.App.RestApi;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class AddressChangeStepDefinitions(OrderingContext orderingContext, OrderingApiDriver orderingApiDriver)
{
    private TestActionResult<VoidReturn> addressChangeResult = TestActionResult<VoidReturn>.NotExecuted;

    [When("they attempt to change the delivery address")]
    public async Task WhenTheyAttemptToChangeTheDeliveryAddress()
    {
        int orderNo = orderingContext.PlacedOrderNo ?? throw new InvalidOperationException("No order placed.");
        addressChangeResult = await orderingApiDriver
            .ChangeDeliveryAddress(orderNo, new ChangeAddressRequest(DomainDefaults.AltDeliveryAddress))
            .AttemptExecute();
    }

    [Then("the address change should be allowed")]
    public void ThenTheAddressChangeShouldBeAllowed()
    {
        addressChangeResult.AssertSucceeded();
    }
}
