using System.Collections;
using System.Globalization;
using System.Text;

using Reqnroll;

namespace WIMP.Specs.Support;

public class TestFileSystem(IFeatureContext featureContext, IScenarioContext scenarioContext)
{
    public static readonly string TestRunTimestamp =
        ToPath(DateTime.Now.ToString("s", CultureInfo.InvariantCulture));

    public string OutputFolder =>
        EnsureFolderExists(Path.Combine(Directory.GetCurrentDirectory(), TestRunTimestamp));

    public string InputFolder => AppContext.BaseDirectory;

    public string FeatureInputFolder =>
        Path.Combine(InputFolder, featureContext.FeatureInfo.FolderPath);

    public string TempFolder => EnsureFolderExists(
        Path.Combine(Path.GetTempPath(), "WIMP", TestRunTimestamp));

    public string GetScenarioSpecificFileName(string extension = "")
    {
        string featureAsPath = ToPath(featureContext.FeatureInfo.Title);
        string scenarioAsPath = ToPath(scenarioContext.ScenarioInfo.Title);
        string baseFileName = $"{featureAsPath}_{scenarioAsPath}";
        if (scenarioContext.ScenarioInfo.Arguments is { Count: > 0 })
        {
            foreach (DictionaryEntry entry in
                     scenarioContext.ScenarioInfo.Arguments)
            {
                baseFileName += $"_{entry.Key}-{entry.Value}";
            }
        }
        return baseFileName + extension;
    }

    /// <summary>
    /// Makes string path-compatible, ie removes characters not allowed in path and replaces whitespace with '_'
    /// </summary>
    public static string ToPath(string s)
    {
        var builder = new StringBuilder(s);
        foreach (char invalidChar in Path.GetInvalidFileNameChars())
        {
            builder.Replace(invalidChar.ToString(), "");
        }
        builder.Replace(' ', '_');
        return builder.ToString();
    }

    private string EnsureFolderExists(string path)
    {
        if (!Directory.Exists(path))
        {
            lock (this)
            {
                if (!Directory.Exists(path))
                {
                    Directory.CreateDirectory(path);
                }
            }
        }
        return path;
    }
}
