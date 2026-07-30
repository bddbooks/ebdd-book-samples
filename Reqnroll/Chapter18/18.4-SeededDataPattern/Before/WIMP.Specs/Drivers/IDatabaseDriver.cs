namespace WIMP.Specs.Drivers;

public interface IDatabaseDriver
{
    public Task EmptyDatabase();
    public Task UpgradeSchemaIfNeeded();
}
