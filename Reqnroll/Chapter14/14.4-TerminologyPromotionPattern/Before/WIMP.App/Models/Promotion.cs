namespace WIMP.App.Models;

public class Promotion(string name, bool isActive)
{
    public string Name { get; set; } = name;
    public bool IsActive { get; set; } = isActive;
}
