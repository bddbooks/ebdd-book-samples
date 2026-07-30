using System.Text;

using WIMP.App.Models;

namespace WIMP.Specs.Support;

public static class ReportPrinter
{
    public static string PrintReport(SalesReport report)
    {
        var result = new StringBuilder();

        result.AppendLine("* By Pizza");
        foreach (var pizzaReport in report.SalesByPizza.OrderBy(e => e.Key))
        {
            result.AppendLine($"  * {pizzaReport.Key}: {FormatValue(pizzaReport.Value)}");
        }

        result.AppendLine("* By Day");
        foreach (var dayReport in report.SalesByDay.OrderBy(e => e.Key))
        {
            result.AppendLine($"  * {dayReport.Key:yyyy-MM-dd}: {FormatValue(dayReport.Value)}");
        }

        result.AppendLine($"* Total: {FormatValue(report.TotalSales)}");

        return result.ToString().TrimEnd();
    }

    private static string FormatValue(SalesReportValue value) =>
        $"${value.Sales:0.##} ({value.Percentage:0}%)";
}
