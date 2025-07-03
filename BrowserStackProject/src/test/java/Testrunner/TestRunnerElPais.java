package Testrunner;

import config.Setup;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

@CucumberOptions(
        features = "src/test/java/feature/ElPaisFeature.feature",
        glue      = "stepDefintion",
        plugin    = {"pretty", "html:target/cucumber-reports.html"},
        monochrome = true
)
public class TestRunnerElPais extends AbstractTestNGCucumberTests {

    @BeforeClass(alwaysRun = true)
    @Parameters({"os", "osVersion", "browser", "browserVersion", "device"})
    public void initDriver(@Optional("") String os,
                           @Optional("") String osVersion,
                           @Optional("") String browser,
                           @Optional("") String browserVersion,
                           @Optional("") String device) {

        Setup.initDriverForThread(os, osVersion, browser, browserVersion, device);
    }


}
