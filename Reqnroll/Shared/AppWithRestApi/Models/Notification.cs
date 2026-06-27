namespace WIMP.App.Models;

public class Notification
{
    public string CustomerName { get; set; } = string.Empty;
    public string Message { get; set; } = string.Empty;
    public DateTimeOffset SentAt { get; set; } = DateTimeOffset.UtcNow;
}
