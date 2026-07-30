using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(OrderingApiDriver orderingApiDriver)
{
    private TestActionResult<Order> placeOrderResult = TestActionResult<Order>.NotExecuted;

    [When("they place an order for {int} pizzas of size {PizzaSize}")]
    public async Task WhenTheyPlaceAnOrderForPizzasOfSize(int count, PizzaSize size)
    {
        var orderRequest = new PlaceOrderRequestObjectMother()
            .WithItems(count, size: size)
            .Build();
        placeOrderResult = await orderingApiDriver
            .PlaceOrder(orderRequest)
            .AttemptExecute();
    }

    [Then("the order should be rejected with message {string}")]
    public void ThenTheOrderShouldBeRejectedWithMessage(string expectedMessage)
    {
        placeOrderResult.AssertFailedWithErrorMessageContains(expectedMessage);
    }
}
