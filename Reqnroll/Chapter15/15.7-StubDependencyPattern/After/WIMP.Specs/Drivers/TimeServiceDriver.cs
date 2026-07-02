using WIMP.Specs.Support;

namespace WIMP.Specs.Drivers;

public class TimeServiceDriver(StubTimeService stubTimeService)
{
    public void SetCurrentTime(TimeOnly time)
    {
        var dateTime = stubTimeService.GetCurrentTime().WithTime(time);
        stubTimeService.SetCurrentTime(dateTime);
    }

    public DateTimeOffset GetTodayTime(TimeOnly time)
    {
        return stubTimeService.GetCurrentTime().WithTime(time);
    }
}
