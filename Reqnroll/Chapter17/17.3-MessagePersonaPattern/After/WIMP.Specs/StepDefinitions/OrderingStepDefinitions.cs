using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, AuthenticationApiDriver authenticationApiDriver)
{
    private TestActionResult<Order> placeOrderResult = TestActionResult<Order>.NotExecuted;

    [When("the customer places an order for {int} pizzas of size {PizzaSize}")]
    public async Task WhenTheCustomerPlacesAnOrderForPizzasOfSize(int count, PizzaSize size)
    {
        await authenticationApiDriver.Login(DomainDefaults.CustomerName, DomainDefaults.Password).Execute();

        var orderRequest = new PlaceOrderRequestObjectMother()
            .WithItems(count, size: size)
            .Build();
        placeOrderResult = await orderingApiDriver
            .PlaceOrder(orderRequest)
            .AttemptExecute();
    }

    [Then("the order should be rejected with message {user-message}")]
    public void ThenTheOrderShouldBeRejectedWith(string expectedMessage)
    {
        placeOrderResult.AssertFailedWithErrorMessageContains(expectedMessage);
    }
}
