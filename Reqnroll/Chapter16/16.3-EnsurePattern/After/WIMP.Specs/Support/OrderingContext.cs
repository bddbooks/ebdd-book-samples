using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

public class OrderingContext(OrderingApiDriver orderingApiDriver)
{
    public int? CurrentOrderNo { get; set; }

    public async Task EnsureOrderPlaced()
    {
        if (CurrentOrderNo == null)
        {
            var orderRequest = new PlaceOrderRequestObjectMother().Build();
            var placedOrder = await orderingApiDriver
                .PlaceOrder(orderRequest)
                .Execute();
            CurrentOrderNo = placedOrder.OrderNo;
        }
    }
}
