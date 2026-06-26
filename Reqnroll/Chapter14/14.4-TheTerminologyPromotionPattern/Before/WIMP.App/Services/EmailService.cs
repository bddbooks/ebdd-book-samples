using WIMP.App.Infrastructure;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class EmailService(DataContext dataContext)
{
    public void SendCouponEmail(string customerName, string couponCode)
    {
        dataContext.InsertCouponEmail(new CouponEmail(customerName, couponCode));
    }

    public bool WasCouponSent(string customerName, string couponCode)
    {
        return dataContext.GetCouponEmailsByCustomer(customerName)
            .Any(c => c.Code == couponCode);
    }
}
