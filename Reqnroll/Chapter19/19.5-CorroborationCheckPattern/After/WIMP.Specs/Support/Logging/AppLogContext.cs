using System.Collections.Concurrent;

using Microsoft.Extensions.Logging;

namespace WIMP.Specs.Support.Logging;

/// <summary>
/// Scenario context to collect log entries from the hosted WIMP application.
/// The log entries can be saved to an attachment or can be used for diagnoses.
/// </summary>
public class AppLogContext
{
    private readonly ConcurrentQueue<string> logMessages = new();
    private readonly ConcurrentQueue<string> healthIssues = new();

    public IReadOnlyCollection<string> LogMessages => logMessages;

    public void AddLogMessage(LogLevel logLevel, string logMessage)
    {
        logMessages.Enqueue(logMessage);
        if (logLevel >= LogLevel.Warning)
        {
            healthIssues.Enqueue(logMessage);
        }
    }

    public void CheckAppHealth()
    {
        if (healthIssues.Any())
        {
            string issueText = string.Join(Environment.NewLine, healthIssues);
            throw new CorroborationCheckException(
                $"The application log contains warnings or errors:{Environment.NewLine}{issueText}");
        }
    }

    /// <summary>
    /// Suppresses the health issues that have been collected so far. This can
    /// be used for special tests where application warnings or errors are
    /// expected.
    /// </summary>
    public void SuppressAppHealthIssues()
    {
        healthIssues.Clear();
    }

    public void SaveToFile(string outputPath)
    {
        File.WriteAllLines(outputPath, logMessages.ToArray());
    }
}
