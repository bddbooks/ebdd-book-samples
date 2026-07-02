using WIMP.App.Services;

namespace WIMP.Specs.Support;

public class StubTimeService : ITimeService
{
    private DateTimeOffset now = DateTimeOffset.Now;
    private readonly List<Func<DateTimeOffset, bool>> timeChangeSubscribers = [];

    public DateTimeOffset GetCurrentTime() => now;

    public void SubscribeToTimeChange(Func<DateTimeOffset, bool> onTimeChanged) =>
        timeChangeSubscribers.Add(onTimeChanged);

    public void SetCurrentTime(DateTimeOffset currentDateTime)
    {
        now = currentDateTime;
        TriggerTimeChange();
    }

    public void TriggerTimeChange()
    {
        var currentDateTime = GetCurrentTime();
        foreach (var subscriber in timeChangeSubscribers.ToArray())
        {
            if (subscriber(currentDateTime))
            {
                timeChangeSubscribers.Remove(subscriber);
            }
        }
    }
}
