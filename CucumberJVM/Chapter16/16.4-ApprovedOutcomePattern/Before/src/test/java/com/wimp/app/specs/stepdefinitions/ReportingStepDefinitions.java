package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.SalesReport;
import com.wimp.app.specs.drivers.BackdoorApiDriver;
import com.wimp.app.specs.drivers.ReportingApiDriver;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ReportingStepDefinitions {
    private final BackdoorApiDriver backdoorApiDriver;
    private final ReportingApiDriver reportingApiDriver;
    private SalesReport generatedReport;

    public ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver) {
        this.backdoorApiDriver = backdoorApiDriver;
        this.reportingApiDriver = reportingApiDriver;
    }

    @Given("a normal week of operations with these pizza sales:")
    public void aWeekOfOperationsWithThesePizzaSales(DataTable salesTable) throws Exception {
        backdoorApiDriver.prepareSalesTraffic(salesTable).execute();
    }

    @When("the sales report is requested for the week beginning {date}")
    public void theSalesReportIsRequestedForTheWeekBeginning(LocalDate startDay) throws Exception {
        generatedReport = reportingApiDriver.generateSalesReport(startDay).execute();
    }

    @Then("the report should show a total sales volume of {price}")
    public void theReportShouldShowATotalSalesVolumeOf(BigDecimal expectedTotalSales) {
        assertNotNull(generatedReport, "Report is not generated.");
        assertThat(generatedReport.totalSales().sales()).as("Unexpected total sales.").isEqualByComparingTo(expectedTotalSales);
    }

    private void assertPizzaSales(String pizzaName, BigDecimal expectedSales) {
        assertTrue(
            generatedReport.salesByPizza().containsKey(pizzaName),
            "Sales data for pizza '%s' was not found in the report.".formatted(pizzaName));
        assertThat(generatedReport.salesByPizza().get(pizzaName).sales()).as("Unexpected sales for '%s'.".formatted(pizzaName)).isEqualByComparingTo(expectedSales);
    }

    @And("there should be {price} Pepperoni, {price} Margherita, and {price} BBQ sales on the report")
    public void thereShouldBe$Pepperoni$MargheritaAnd$BBQSalesOnTheReport(
        BigDecimal pepperoniSales, BigDecimal margheritaSales, BigDecimal bbqSales) {

        assertNotNull(generatedReport, "Report is not generated.");
        assertPizzaSales("Pepperoni", pepperoniSales);
        assertPizzaSales("Margherita", margheritaSales);
        assertPizzaSales("BBQ", bbqSales);
    }

    @And("there should be a by day breakdown on the report")
    public void thereShouldBeAByDayBreakdownOnTheReport() {
        assertNotNull(generatedReport, "Report is not generated.");
        assertEquals(7, generatedReport.salesByDay().size(), "Expected sales data for 7 days.");
    }

    @And("all values should be also shown as percentages of the total")
    public void allValuesShouldBeAlsoShownAsPercentagesOfTheTotal() {
        assertNotNull(generatedReport, "Report is not generated.");
        assertThat(generatedReport.totalSales().percentage()).as("Expected total sales percentage to be 100%").isEqualByComparingTo(new BigDecimal(100));
        for (var pizzaReport: generatedReport.salesByPizza().entrySet())
        {
            assertThat(pizzaReport.getValue().percentage()).as("Expected percentage for %s to be between 0%% and 100%%".formatted(pizzaReport.getKey()))
                .isBetween(BigDecimal.ZERO, new BigDecimal(100));
        }
        for (var dayReport: generatedReport.salesByDay().entrySet())
        {
            assertThat(dayReport.getValue().percentage()).as("Expected percentage for %s to be between 0%% and 100%%".formatted(dayReport.getKey()))
                .isBetween(BigDecimal.ZERO, new BigDecimal(100));
        }
    }
}
