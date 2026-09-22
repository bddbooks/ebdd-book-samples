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
