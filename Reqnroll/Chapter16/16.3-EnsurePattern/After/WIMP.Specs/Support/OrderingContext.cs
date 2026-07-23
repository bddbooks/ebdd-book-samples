using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

public class OrderingContext(OrderingApiDriver orderingApiDriver)
{
    public int? PlacedOrderNo { get; set; }

    public async Task EnsureOrderPlaced()
    {
        if (PlacedOrderNo == null)
        {
            var orderRequest = new PlaceOrderRequestObjectMother().Build();
            var placedOrder = await orderingApiDriver
                .PlaceOrder(orderRequest)
                .Execute();
            PlacedOrderNo = placedOrder.OrderNo;
        }
    }
}
