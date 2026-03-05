namespace WIMP.IntroduceAndReferSample.App.Models;

public class Notification(string customerName, string message)
{
    public string CustomerName { get; set; } = customerName;
    public string Message { get; set; } = message;
}
