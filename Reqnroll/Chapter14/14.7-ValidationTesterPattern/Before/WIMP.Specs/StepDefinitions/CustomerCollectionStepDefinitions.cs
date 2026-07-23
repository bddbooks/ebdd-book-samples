using Reqnroll;

using WIMP.App.Models;
using WIMP.App.Services;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class CustomerCollectionStepDefinitions(OrderService orderService)
{
    private Order? order;
    private Exception? provideContactDetailsError = new InvalidOperationException("provide contact details was not invoked");

    [Given("a customer has chosen to collect their order")]
    public void GivenACustomerHasChosenToCollectTheirOrder()
    {
        order = new OrderObjectMother().WithCustomerCollection().Build();
    }

    [When("the customer provides the contact details as:")]
    public void WhenTheCustomerProvidesTheContactDetailsAs(DataTable contactDetailsTable)
    {
        var contactDetails = contactDetailsTable.CreateInstance<ContactDetails>();
        ProvideContactDetails(contactDetails);
    }

    private void ProvideContactDetails(ContactDetails contactDetails)
    {
        try
        {
            provideContactDetailsError = null;
            orderService.ProvideContactDetails(
                order ?? throw new InvalidOperationException("Order not placed"),
                contactDetails);
        }
        catch (Exception ex)
        {
            provideContactDetailsError = ex;
        }
    }

    [Then("the contact details are accepted")]
    public void ThenTheContactDetailsAreAccepted()
    {
        Assert.IsNull(provideContactDetailsError, $"No error expected, but got: {provideContactDetailsError?.Message}");
    }

    [Then("the contact details are not accepted")]
    public void ThenTheContactDetailsAreNotAccepted()
    {
        Assert.IsNotNull(provideContactDetailsError);
    }
}
