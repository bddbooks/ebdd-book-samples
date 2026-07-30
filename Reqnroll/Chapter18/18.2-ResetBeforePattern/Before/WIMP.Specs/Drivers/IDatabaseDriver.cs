namespace WIMP.Specs.Drivers;

public interface IDatabaseDriver
{
    public Task CreateDatabase();
    public Task DropDatabase();
}
