/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

using WIMP.App.Data;
using WIMP.App.Models;

namespace WIMP.App.Services;

public class ReportingService(IDataRepository dataRepository)
{
    public SalesReport GenerateSalesReport(DateOnly startDay)
    {
        var salesData = dataRepository.GetDailyPizzaSalesBetweenDates(startDay, startDay.AddDays(6))
            .OrderBy(daySales => daySales.Date)
            .ToArray();

        decimal totalSales = salesData.Sum(d => d.Sales);
        return new SalesReport
        {
            TotalSales = new SalesReportValue(totalSales, 100),
            SalesByPizza = salesData
                .GroupBy(d => d.Pizza)
                .ToDictionary(g => g.Key, g => CreateReportValue(g.Sum(d => d.Sales), totalSales)),
            SalesByDay = salesData
                .GroupBy(d => d.Date)
                .ToDictionary(g => g.Key, g => CreateReportValue(g.Sum(d => d.Sales), totalSales))
        };
    }

    private static SalesReportValue CreateReportValue(decimal sales, decimal totalSales)
    {
        decimal percentage = totalSales == 0 ? 0 : sales / totalSales * 100;
        return new SalesReportValue(sales, percentage);
    }
}
