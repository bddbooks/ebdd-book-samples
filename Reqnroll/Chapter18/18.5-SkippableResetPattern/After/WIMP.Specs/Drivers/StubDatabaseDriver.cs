namespace WIMP.Specs.Drivers;

public class StubDatabaseDriver : IDatabaseDriver
{
    public Task EmptyDatabase(IReadOnlyCollection<string>? exceptTables = null)
    {
        //nop
        return Task.CompletedTask;
    }

    public Task UpgradeSchemaIfNeeded()
    {
        //nop
        return Task.CompletedTask;
    }

    public Task<bool> WasTableModified(string tableName)
    {
        return Task.FromResult(true); // treat it modified, seeding is anyway "free"
    }

    public Task ResetTableModificationTracking()
    {
        //nop
        return Task.CompletedTask;
    }
}
