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
        var pizzaItem = DomainDefaults.PizzaItemDefaultInstance();
        pizzaItem.Name = pizzaName;
        order.AddItem(pizzaItem);
        order.DeliveryAddress = DomainDefaults.DeliveryAddress;

        AuthenticationService.Login(DomainDefaults.CustomerName);
        var placedOrder = orderService.PlaceOrder(order);
        orderingContext.PlacedOrderNo = placedOrder.OrderNo;
    }

    [When("the customer places an order for {int} pizza(s) of size {string}")]
    public void WhenTheCustomerPlacesAnOrderFor(int count, string pizzaSize)
    {
        var order = new Order();
        for (int i = 0; i < count; i++)
        {
            var pizzaItem = DomainDefaults.PizzaItemDefaultInstance();
            pizzaItem.Size = pizzaSize;
            order.AddItem(pizzaItem);
        }
        order.DeliveryAddress = DomainDefaults.DeliveryAddress;

        AuthenticationService.Login(DomainDefaults.CustomerName);
        var placedOrder = orderService.PlaceOrder(order);
        orderingContext.PlacedOrderNo = placedOrder.OrderNo;
    }

    [Given("the customer has placed an order")]
    public void GivenTheCustomerHasPlacedAnOrder()
    {
        var order = new Order();
        var pizzaItem = DomainDefaults.PizzaItemDefaultInstance();
        order.AddItem(pizzaItem);
        order.DeliveryAddress = DomainDefaults.DeliveryAddress;

        AuthenticationService.Login(DomainDefaults.CustomerName);
        var placedOrder = orderService.PlaceOrder(order);
        orderingContext.PlacedOrderNo = placedOrder.OrderNo;
    }

    #region Additional Step Definitions

    private Exception? deliveryAddressChangeError = new InvalidOperationException("address change was not invoked");

    [Given("the {string} promotion is active")]
    public void GivenThePromotionIsActive(string promotionName)
    {
        promotionService.ActivatePromotion(promotionName);
    }

    [When("the order is delivered")]
    public void WhenTheOrderIsDelivered()
    {
        int orderNo = orderingContext.PlacedOrderNo ?? throw new InvalidOperationException("Order not placed");
        orderService.DeliverOrder(orderNo);
    }

    [Then("the customer should receive a {string} coupon via email")]
    public void ThenTheCustomerShouldReceiveACouponViaEmail(string couponCode)
    {
        Assert.IsTrue(emailService.WasCouponSent(DomainDefaults.CustomerName, couponCode));
    }

    [Then("the order should be rejected")]
    public void ThenTheOrderShouldBeRejected()
    {
        int orderNo = orderingContext.PlacedOrderNo ?? throw new InvalidOperationException("Order not placed");
        var order = orderService.GetOrder(orderNo) ?? throw new InvalidOperationException("Order not found");
        Assert.AreEqual(OrderStatus.Rejected, order.Status);
    }

    [Given("the order is waiting for pickup")]
    public void GivenTheOrderIsWaitingForPickup()
    {
        int orderNo = orderingContext.PlacedOrderNo ?? throw new InvalidOperationException("Order not placed");
        orderService.SetWaitingForPickup(orderNo);
    }

    [When("the customer requests to change the delivery address")]
    public void WhenTheCustomerRequestsToChangeTheDeliveryAddress()
    {
        int orderNo = orderingContext.PlacedOrderNo ?? throw new InvalidOperationException("Order not placed");
        try
        {
            deliveryAddressChangeError = null;
            orderService.ChangeDeliveryAddress(orderNo, DomainDefaults.AltDeliveryAddress);
        }
        catch (Exception ex)
        {
            deliveryAddressChangeError = ex;
        }
    }

    [Then("the address change should be allowed")]
    public void ThenTheAddressChangeShouldBeAllowed()
    {
        Assert.IsNull(deliveryAddressChangeError);
    }

    #endregion
}
