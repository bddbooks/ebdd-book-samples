namespace WIMP.Specs.Support;

public static class DateTimeOffsetExtensions
{
    public static DateTimeOffset WithTime(this DateTimeOffset dateTimeOffset, TimeOnly time) =>
        new(DateOnly.FromDateTime(dateTimeOffset.Date), time, dateTimeOffset.Offset);

    public static DateTimeOffset WithTime(this DateTimeOffset dateTimeOffset, TimeSpan time) =>
        dateTimeOffset.WithTime(TimeOnly.FromTimeSpan(time));
}
