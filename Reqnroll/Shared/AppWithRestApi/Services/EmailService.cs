using WIMP.App.Data;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class EmailService(IDataRepository repository)
{
    public void SendCoupon(string customerEmail, string couponCode)
    {
        repository.InsertCoupon(new Coupon { Code = couponCode, CustomerEmail = customerEmail });
    }

    public bool WasCouponSent(string customerEmail, string couponCode) =>
        repository.GetCouponsByEmail(customerEmail).Any(c =>
            string.Equals(c.Code, couponCode, StringComparison.OrdinalIgnoreCase));

    public IEnumerable<Coupon> GetCoupons(string customerEmail) =>
        repository.GetCouponsByEmail(customerEmail);
}
