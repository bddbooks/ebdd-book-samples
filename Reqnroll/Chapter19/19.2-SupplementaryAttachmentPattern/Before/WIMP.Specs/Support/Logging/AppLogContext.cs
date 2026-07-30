using System.Collections.Concurrent;

namespace WIMP.Specs.Support.Logging;

/// <summary>
/// Scenario context to collect log entries from the hosted WIMP application.
/// The log entries can be saved to an attachment or can be used for diagnoses.
/// </summary>
public class AppLogContext
{
    private readonly ConcurrentQueue<string> logMessages = new();

    public IReadOnlyCollection<string> LogMessages => logMessages;

    public void AddLogMessage(string logMessage)
    {
        logMessages.Enqueue(logMessage);
    }

    public void SaveToFile(string outputPath)
    {
        File.WriteAllLines(outputPath, logMessages.ToArray());
    }
}
