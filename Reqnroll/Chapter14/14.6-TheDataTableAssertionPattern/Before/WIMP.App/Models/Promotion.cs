namespace WIMP.App.Models;

public class Promotion(string name, bool isActive, IReadOnlyList<OfferedItem> offeredItems)
{
    public string Name { get; } = name;
    public bool IsActive { get; } = isActive;
    public IReadOnlyList<OfferedItem> OfferedItems { get; } = offeredItems;
}
