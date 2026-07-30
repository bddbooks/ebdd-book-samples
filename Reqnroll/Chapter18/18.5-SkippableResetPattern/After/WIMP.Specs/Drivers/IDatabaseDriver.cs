namespace WIMP.Specs.Drivers;

public interface IDatabaseDriver
{
    public Task EmptyDatabase(IReadOnlyCollection<string>? exceptTables = null);
    public Task UpgradeSchemaIfNeeded();
    public Task<bool> WasTableModified(string tableName);
    public Task ResetTableModificationTracking();
}
