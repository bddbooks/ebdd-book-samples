using Reqnroll;

using WIMP.App.Services;
using WIMP.Specs.Drivers;

namespace WIMP.Specs.Support;

[Binding]
public class CustomParameterTypes(CustomerDriver customerDriver, MessageService messageService)
{
    [StepArgumentTransformation(@"\[([\w\-]+(?:,.+)?)\]", Name = "user-message")]
    public string ConvertOrder(string messageNameSpecification)
    {
        string language = customerDriver.GetInterfaceLanguage();
        string[] specParts = messageNameSpecification.Split(',');
        string messageName = specParts[0];
        string[] parameters = specParts.Skip(1).ToArray();
        return messageService.GetMessage(language, messageName, parameters);
    }
}
