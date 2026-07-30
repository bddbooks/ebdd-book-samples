namespace WIMP.Specs.Drivers;

public class StubDatabaseDriver : IDatabaseDriver
{
    public Task EmptyDatabase()
    {
        //nop
        return Task.CompletedTask;
    }

    public Task UpgradeSchemaIfNeeded()
    {
        //nop
        return Task.CompletedTask;
    }
}
