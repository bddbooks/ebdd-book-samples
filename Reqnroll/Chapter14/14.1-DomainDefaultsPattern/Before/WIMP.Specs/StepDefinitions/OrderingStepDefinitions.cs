using Reqnroll;

using WIMP.App.Models;
using WIMP.App.Services;
using WIMP.Specs.Support;

namespace WIMP.Specs.StepDefinitions;

[Binding]
public class OrderingStepDefinitions(OrderingContext orderingContext, OrderService orderService, PromotionService promotionService, EmailService emailService)
{
    [Given("the customer has placed an order containing a {string} pizza")]
    public void GivenTheCustomerHasPlacedAnOrderContaining(string pizzaName)
    {
        var order = new Order();
        order.AddItem(pizzaName, "Medium");  // Hard-coded default value

        AuthenticationService.Login("Rebecca");
        var placedOrder = orderService.PlaceOrder(order);
        orderingContext.CurrentOrderNo = placedOrder.OrderNo;
    }

    #region Additional Step Definitions

    [Given("the {string} promotion is active")]
    public void GivenThePromotionIsActive(string promotionName)
    {
        promotionService.ActivatePromotion(promotionName);
    }

    [When("the order is delivered")]
    public void WhenTheOrderIsDelivered()
    {
        int orderNo = orderingContext.CurrentOrderNo ?? throw new InvalidOperationException("No current order");
        orderService.DeliverOrder(orderNo);
    }

    [Then("the customer should receive a {string} coupon via email")]
    public void ThenTheCustomerShouldReceiveACouponViaEmail(string couponCode)
    {
        Assert.IsTrue(emailService.WasCouponSent("Rebecca", couponCode));
    }

    #endregion
}
