namespace WIMP.Specs.Drivers;

public class StubDatabaseDriver : IDatabaseDriver
{
    public Task CreateDatabase()
    {
        //nop
        return Task.CompletedTask;
    }

    public Task DropDatabase()
    {
        //nop
        return Task.CompletedTask;
    }
}
