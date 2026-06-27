using System.Collections.Concurrent;

namespace WIMP.App.Services;

public interface ITimeService
{
    public DateTimeOffset GetCurrentTime();
    public void SubscribeToTimeChange(Func<DateTimeOffset, bool> onTimeChanged);
}

public class TimeService : ITimeService, IDisposable
{
    private readonly CancellationTokenSource cancellationTokenSource = new();
    private readonly ConcurrentDictionary<Func<DateTimeOffset, bool>, bool> timeChangeSubscribers = new();

    public TimeService()
    {
        Task.Run(() => RunAsync(cancellationTokenSource.Token));
    }

    private async Task RunAsync(CancellationToken cancellationToken)
    {
        try
        {
            await Task.Delay(TimeSpan.FromSeconds(1), cancellationToken);

            while (!cancellationToken.IsCancellationRequested)
            {
                var currentTime = GetCurrentTime();
                foreach (var subscriber in timeChangeSubscribers.Keys)
                {
                    bool processed = subscriber(currentTime);
                    if (processed)
                    {
                        timeChangeSubscribers.TryRemove(subscriber, out _);
                    }
                }

                await Task.Delay(TimeSpan.FromSeconds(1), cancellationToken);
            }
        }
        catch (OperationCanceledException) when (cancellationToken.IsCancellationRequested)
        {
            // normal shutdown
        }
    }

    public DateTimeOffset GetCurrentTime() =>
        DateTimeOffset.Now;

    public void SubscribeToTimeChange(Func<DateTimeOffset, bool> onTimeChanged) =>
        timeChangeSubscribers.TryAdd(onTimeChanged, true);

    public void Dispose()
    {
        cancellationTokenSource.Cancel();
        // Optionally wait for the timer task to complete
    }
}
