using Reqnroll;

namespace WIMP.Specs.Support;

[Binding]
public class CustomParameterTypes
{
    [StepArgumentTransformation(@"(\d+-\d+-\d+)")]
    public DateOnly ConvertDateOnly(string value)
    {
        return DateOnly.Parse(value);
    }
}
