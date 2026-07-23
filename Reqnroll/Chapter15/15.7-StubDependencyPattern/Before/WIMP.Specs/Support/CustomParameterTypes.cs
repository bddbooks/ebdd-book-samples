using Reqnroll;

namespace WIMP.Specs.Support;

[Binding]
public class CustomParameterTypes
{
    [StepArgumentTransformation(@"(\d+:\d+)")]
    public static TimeOnly ConvertTimeOnly(string value) =>
        TimeOnly.Parse(value);
}
