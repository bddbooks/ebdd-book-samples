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

public static class NotificationService
{
    public static void SendCancellationNotification(string customerName)
    {
        DataContext.Instance.SaveNotification(new Notification(customerName, "Your order has been cancelled."));
    }

    public static bool WasNotificationSent(string? customerName)
    {
        return DataContext.Instance.GetNotificationsByCustomerName(customerName ?? "n/a").Any();
    }
}
