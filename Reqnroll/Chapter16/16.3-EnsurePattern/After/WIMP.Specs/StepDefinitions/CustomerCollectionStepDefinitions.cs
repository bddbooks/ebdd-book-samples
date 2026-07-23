using Reqnroll;

using WIMP.App.Models;
using WIMP.Specs.Drivers;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class CustomerCollectionStepDefinitions(
    AuthenticationApiDriver authApiDriver,
    OrderingApiDriver orderingApiDriver,
    AuthenticationContext authenticationContext,
    OrderingContext orderingContext)
{
    private OrderCollectionDetails? orderCollectionDetails;

    [Given("the customer is authenticated")]
    public async Task GivenTheCustomerIsAuthenticated()
    {
        await authApiDriver
            .Login(DomainDefaults.CustomerName, DomainDefaults.Password)
            .Execute();
        authenticationContext.LoggedInCustomerName = DomainDefaults.CustomerName;
    }

    [Given("they have placed an order for {int} pizzas")]
    public async Task GivenTheyHavePlacedAnOrderForPizzas(int count)
    {
        var orderRequest = new PlaceOrderRequestObjectMother()
            .WithItems(count)
            .Build();
        var placedOrder = await orderingApiDriver
            .PlaceOrder(orderRequest)
            .Execute();
        orderingContext.PlacedOrderNo = placedOrder.OrderNo;
    }

    [When("they choose to collect their order")]
    public async Task WhenTheyChooseToCollectTheirOrder()
    {
        await orderingContext.EnsureOrderPlaced();

        orderCollectionDetails = await orderingApiDriver
            .SetForCollection(orderingContext.PlacedOrderNo ?? throw new InvalidOperationException("No order placed."))
            .Execute();
    }

    [Then("they should be asked to confirm contact details")]
    public void ThenTheyShouldBeAskedToConfirmContactDetails()
    {
        Assert.IsNotNull(orderCollectionDetails, "The order was not set to customer-collection");
        Assert.IsTrue(orderCollectionDetails.ContactDetailsConfirmationRequested);
    }

    [Then("a collection receipt should be printed with")]
    public void ThenACollectionReceiptShouldBePrintedWith(DataTable dataTable)
    {
        Assert.IsNotNull(orderCollectionDetails, "The order was not set to customer-collection");
        int expectedBoxes = int.Parse(dataTable.Rows[0]["boxes to be collected"]);
        Assert.AreEqual(expectedBoxes, orderCollectionDetails.BoxesToBeCollected, "Invalid number of boxes for customer-collection");
    }
}
