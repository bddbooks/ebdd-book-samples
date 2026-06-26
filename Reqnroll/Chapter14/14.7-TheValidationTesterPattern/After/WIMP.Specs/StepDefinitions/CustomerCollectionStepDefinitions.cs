using Reqnroll;

using WIMP.App.Models;
using WIMP.App.Services;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class CustomerCollectionStepDefinitions(OrderService orderService)
{
    private Order? order;
    private Exception? provideCustomerDetailsError = new InvalidOperationException("provide customer details was not invoked");

    [Given("a customer has chosen to collect their order")]
    public void GivenACustomerHasChosenToCollectTheirOrder()
    {
        order = new OrderObjectMother().WithCustomerCollection().Build();
    }

    [When("the customer provides valid contact details, but:")]
    public void WhenTheCustomerProvidesValidContactDetailsBut(DataTable customizationTable)
    {
        var contactDetails = DomainDefaults.ContactDetailsDefaultInstance();
        customizationTable.FillInstance(contactDetails);

        ProvideCustomerDetails(contactDetails);
    }

    [When("the customer provides the contact details as:")]
    public void WhenTheCustomerProvidesTheContactDetailsAs(DataTable contactDetailsTable)
    {
        var contactDetails = contactDetailsTable.CreateInstance<ContactDetails>();
        ProvideCustomerDetails(contactDetails);
    }

    private void ProvideCustomerDetails(ContactDetails contactDetails)
    {
        try
        {
            provideCustomerDetailsError = null;
            orderService.ProvideCustomerDetails(
                order ?? throw new InvalidOperationException("Order not placed"),
                contactDetails);
        }
        catch (Exception ex)
        {
            provideCustomerDetailsError = ex;
        }
    }

    [Then("the contact details are accepted")]
    public void ThenTheContactDetailsAreAccepted()
    {
        Assert.IsNull(provideCustomerDetailsError, $"No error expected, but got: {provideCustomerDetailsError?.Message}");
    }

    [Then("the contact details are not accepted")]
    public void ThenTheContactDetailsAreNotAccepted()
    {
        Assert.IsNotNull(provideCustomerDetailsError);
    }
}
