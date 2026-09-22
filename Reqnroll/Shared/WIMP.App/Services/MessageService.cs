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

using System.Text.RegularExpressions;

namespace WIMP.App.Services;

public class MessageService
{
    private const string DefaultLanguage = "en-US";

    /// <summary>
    /// This field represents the message translation dictionary that is normally stored in an external resource file or in a database
    /// </summary>
    private static readonly Dictionary<(string name, string language), string> messageTemplates =
        new()
        {
            { ("cannot-deliver-too-many-large-pizzas",DefaultLanguage), "We cannot deliver %1 large pizzas in a single order, the maximum is 4" },
            { ("invalid-password",DefaultLanguage), "Invalid password" }
        };

    public string GetMessage(string language, string messageName, params object[]? parameters)
    {
        return GetMessage(language, messageName, parameters?.Select(p => p.ToString()).ToArray());
    }

    public string GetMessage(string language, string messageName, params string?[]? parameters)
    {
        string messageTemplate = GetMessageTemplate(language, messageName);
        return parameters == null || parameters.Length == 0
            ? messageTemplate
            : Regex.Replace(messageTemplate, @"%(?<no>\d+)", m =>
            {
                int paramIndex = int.Parse(m.Groups["no"].Value) - 1;
                return paramIndex < 0 || paramIndex >= parameters.Length ? m.Value : parameters[paramIndex] ?? "";
            });
    }

    private string GetMessageTemplate(string language, string messageName)
    {
        return messageTemplates.TryGetValue((messageName, language), out string? template) ||
               messageTemplates.TryGetValue((messageName, DefaultLanguage), out template)
            ? template
            : $"!{messageName} (unspecified message for {language})";
    }
}
