namespace WIMP.Specs.Drivers;

public class StubDatabaseDriver : IDatabaseDriver
{
    public Task EmptyDatabase()
    {
        //nop
        return Task.CompletedTask;
    }
}
