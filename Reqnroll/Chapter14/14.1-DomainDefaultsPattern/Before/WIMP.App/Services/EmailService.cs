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
