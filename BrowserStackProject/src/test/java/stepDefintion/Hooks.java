package stepDefintion; 

import config.Setup;
import io.cucumber.java.*;
import io.cucumber.java.Scenario;
import utils.ExtentReport;

public class Hooks {

    @Before(order = 0)
    public void driverInit() {
//        Setup.initDriverForThread();
    }

    @After(order = 0)
    public void driverQuit() {
        Setup.quitDriver();
    }

    @BeforeAll
    public static void beforeAllTests() { ExtentReport.setupReport(); }

    @Before(order = 1)
    public void beforeScenario(Scenario scenario) {
        ExtentReport.startTest(scenario.getName());
    }

    @AfterStep(order = 1)
    public void afterStep(Scenario scenario) {
        if (scenario.isFailed()) {
            ExtentReport.logStep("Step failed", false);
        }
    }

    @AfterAll
    public static void afterAllTests() { ExtentReport.flushReport(); }
}
