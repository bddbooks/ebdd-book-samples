using Reqnroll;

namespace WIMP.Specs.Support;

[Binding]
public class CustomDataTypes
{
    [StepArgumentTransformation(@"(\d+:\d+)")]
    public static TimeOnly ConvertTimeOnly(string value) =>
        TimeOnly.Parse(value);
}
