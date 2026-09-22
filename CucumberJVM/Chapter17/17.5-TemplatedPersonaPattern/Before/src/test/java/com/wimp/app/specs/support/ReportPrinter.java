package com.wimp.app.specs.support;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import com.wimp.app.models.SalesReport;
import com.wimp.app.models.SalesReportValue;

public final class ReportPrinter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DecimalFormat SALES_FORMATTER = new DecimalFormat("0.##");
    private static final DecimalFormat PERCENT_FORMATTER;

    static {
        PERCENT_FORMATTER = new DecimalFormat("0");
        PERCENT_FORMATTER.setRoundingMode(RoundingMode.HALF_UP);
    }

    private ReportPrinter() {
        // Prevent instantiation for static utility class
    }

    public static String printReport(SalesReport report) {
        StringBuilder result = new StringBuilder();

        result.append("* By Pizza\n");
        report.salesByPizza().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> result.append(String.format("  * %s: %s\n",
                entry.getKey(), formatValue(entry.getValue()))));

        result.append("* By Day\n");
        report.salesByDay().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> result.append(String.format("  * %s: %s\n",
                DATE_FORMATTER.format(entry.getKey()), formatValue(entry.getValue()))));

        result.append(String.format("* Total: %s\n", formatValue(report.totalSales())));

        return result.toString().stripTrailing();
    }

    private static String formatValue(SalesReportValue value) {
        return String.format("$%s (%s%%)",
            SALES_FORMATTER.format(value.sales()),
            PERCENT_FORMATTER.format(value.percentage()));
    }
}
