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
