package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

/**
 * Thread‑safe ExtentReport helper.
 * Keeps one ExtentTest per thread via ThreadLocal, so parallel runs
 * (local or BrowserStack) do not overwrite each other.
 */
public final class ExtentReport {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> TL_TEST = new ThreadLocal<>();

    private ExtentReport() {} // no instances

    /* ── initialise once ─────────────────────────────────────────────── */
    public static void setupReport() {
        if (extent == null) {
            ExtentSparkReporter spark =
                    new ExtentSparkReporter("AutomationReports/extent-report.html");
            spark.config().setDocumentTitle("Automation Test Report");
            spark.config().setReportName("Cucumber Test Execution Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
    }

    /* ── create a node for this scenario/thread ─────────────────────── */
    public static void startTest(String scenarioName) {
        if (extent == null) setupReport();
        TL_TEST.set(extent.createTest(scenarioName));
    }

    /* ── logging helper ─────────────────────────────────────────────── */
    public static void logStep(String detail, boolean pass) {
        ExtentTest t = TL_TEST.get();
        if (t == null) return;                 // safety
        if (pass) t.pass(detail); else t.fail(detail);
    }

    /* ── flush once at the very end ─────────────────────────────────── */
    public static void flushReport() {
        if (extent != null) extent.flush();
    }
}
