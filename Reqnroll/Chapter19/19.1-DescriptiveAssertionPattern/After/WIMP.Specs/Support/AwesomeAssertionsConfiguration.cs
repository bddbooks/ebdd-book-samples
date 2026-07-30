using System.Runtime.CompilerServices;

using AwesomeAssertions.Formatting;

using WIMP.App.Models;

namespace WIMP.Specs.Support;

public class CustomOrderFormatter : IValueFormatter
{
    public bool CanHandle(object value) => value is Order;

    public void Format(object value, FormattedObjectGraph formattedGraph, FormattingContext context, FormatChild formatChild)
    {
        var order = (Order)value;
        formattedGraph.AddFragment($@"order #{order.OrderNo} (placed at {order.PlacingTime:h\:mm\:ss})");
    }
}

public class OrderNumberComparer : IEqualityComparer<Order>
{
    public static readonly OrderNumberComparer Value = new();

    public bool Equals(Order? x, Order? y)
    {
        return x?.OrderNo == y?.OrderNo;
    }

    public int GetHashCode(Order obj)
    {
        return obj.OrderNo;
    }
}

public static class FormatterInitializer
{
    [ModuleInitializer]
    public static void Initialize()
    {
        Formatter.AddFormatter(new CustomOrderFormatter());
    }
}
