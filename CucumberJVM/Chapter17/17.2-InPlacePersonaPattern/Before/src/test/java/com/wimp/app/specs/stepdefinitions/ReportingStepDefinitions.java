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
    private SalesReport generatedReport;

    public ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver) {
        this.backdoorApiDriver = backdoorApiDriver;
        this.reportingApiDriver = reportingApiDriver;
    }

    @Given("a week of operations with these pizza sales:")
    public void aWeekOfOperationsWithThesePizzaSales(DataTable salesTraffic) throws Exception {
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
