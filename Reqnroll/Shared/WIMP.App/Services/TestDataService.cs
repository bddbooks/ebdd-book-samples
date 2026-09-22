/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

using WIMP.App.Data;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class TestDataService(IDataRepository repository, OrderService orderService)
{
    public ServiceResult<Order> PrepareTestOrder(string customerName, string customerEmail, List<PizzaItem> items, string deliveryAddress, OrderStatus status, DateTimeOffset? testOnlyPlacingTime = null, DateTimeOffset? testOnlyExpectedDeliveryTime = null)
    {
        var result = orderService.PlaceOrder(customerName, customerEmail, items, deliveryAddress, testOnlyPlacingTime, testOnlyExpectedDeliveryTime); int largePizzaCount = items.Count(i => i.Size == PizzaSize.Large);
        if (!result.Successful)
        {
            return result;
        }

        var order = result.Value;

        order = repository.UpdateOrder(order, status: status);

        return ServiceResult<Order>.Success(order);
    }

    public ServiceResult<int> PrepareSalesTraffic(IEnumerable<DailyPizzaSales> salesEntries)
    {
#if SIMULATED_BUG
        return ServiceResult<int>.Failure("Simulated error");
#else
        var salesEntryArray = salesEntries.ToArray();
        repository.InsertDailyPizzaSalesEntries(salesEntryArray);
        return ServiceResult<int>.Success(salesEntryArray.Length);
#endif
    }
}
