package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.SalesReport;
import com.wimp.app.specs.drivers.BackdoorApiDriver;
import com.wimp.app.specs.drivers.ReportingApiDriver;
import com.wimp.app.specs.support.ReportPrinter;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ReportingStepDefinitions {
    private final BackdoorApiDriver backdoorApiDriver;
    private final ReportingApiDriver reportingApiDriver;
    private final Map<String, DataTable> salesTrafficSamples = new HashMap<>();
    private SalesReport generatedReport;

    public ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver) {
        this.backdoorApiDriver = backdoorApiDriver;
        this.reportingApiDriver = reportingApiDriver;
    }

    @Given("the {string} weekly sales traffic is")
    public void theWeeklySalesTrafficIs(String name, DataTable salesTraffic) {
        salesTrafficSamples.put(name, salesTraffic);
    }

    @Given("the {string} weekly sales traffic")
    public void theWeeklySalesTraffic(String name) throws Exception {
        var salesTraffic = Optional.ofNullable(salesTrafficSamples.get(name))
            .orElseThrow(() -> new IllegalArgumentException("Traffic " + name + " not known"));
        backdoorApiDriver.prepareSalesTraffic(salesTraffic).execute();
    }

    @When("the sales report is requested for the week beginning {date}")
    public void theSalesReportIsRequestedForTheWeekBeginning(LocalDate startDay) throws Exception {
        generatedReport = reportingApiDriver.generateSalesReport(startDay).execute();
    }

    @Then("the report should show:")
    public void theReportShouldShow(String expectedReport) {
        assertThat(ReportPrinter.printReport(generatedReport).trim().lines().toList())
            .as("The printed report did not match the expected output.")
            .containsExactlyElementsOf(expectedReport.trim().lines().toList());
    }

    @When("the ingredient usage report is requested for the week beginning {date}")
    public void theIngredientUsageReportIsRequestedForTheWeekBeginning(LocalDate startDay) {
      // ingredient usage report generation is not part of this sample
    }

    @Then("the ingredient usage report should show:")
    public void theIngredientUsageReportShouldShow(String expectedReport) {
      // ingredient usage report generation is not part of this sample
    }
}
