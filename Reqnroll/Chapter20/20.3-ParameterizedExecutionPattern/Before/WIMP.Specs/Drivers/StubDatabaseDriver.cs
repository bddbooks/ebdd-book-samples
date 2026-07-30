namespace WIMP.Specs.Drivers;

public class StubDatabaseDriver : IDatabaseDriver
{
    public Task RecreateDatabase()
    {
        //nop
        return Task.CompletedTask;
    }
}
