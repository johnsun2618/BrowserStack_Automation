package config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.edge.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Setup {

    private static final ThreadLocal<WebDriver> TL_DRIVER = new ThreadLocal<>();
    private static final Properties prop = new Properties();
    static {
        try (FileInputStream fis =
                     new FileInputStream("src/main/resources/details.properties")) {
            prop.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load details.properties", e);
        }
    }

    public static WebDriver getDriver() { return TL_DRIVER.get(); }

    public static void quitDriver() {
        if (TL_DRIVER.get() != null) {
            TL_DRIVER.get().quit();
            TL_DRIVER.remove();
        }
    }

    public static void initDriverForThread() {
        if (TL_DRIVER.get() == null) {
            String runMode = System.getProperty("runMode",
                    prop.getProperty("runMode", "local")).toLowerCase();
            TL_DRIVER.set("browserstack".equals(runMode)
                    ? createBrowserStackDriver()
                    : createLocalDriver());
            applyTimeouts();
        }
    }
    public static void initDriverForThread(String os,
                                           String osVersion,
                                           String browser,
                                           String browserVer,
                                           String device) {

        if (TL_DRIVER.get() != null) return;

        String runMode = System.getProperty("runMode",
                prop.getProperty("runMode", "local")).toLowerCase();

        if ("local".equals(runMode)) {
            if (browser != null && !browser.isBlank())
                System.setProperty("browser", browser);

            TL_DRIVER.set(createLocalDriver());
            applyTimeouts();
            return;
        }

        if (os         != null) System.setProperty("bs.os",         os);
        if (osVersion  != null) System.setProperty("bs.osVersion",  osVersion);
        if (browser    != null) System.setProperty("bs.browser",    browser);
        if (browserVer != null) System.setProperty("bs.browserVer", browserVer);
        if (device     != null) System.setProperty("bs.device",     device);

        TL_DRIVER.set(createBrowserStackDriver());
        applyTimeouts();
    }


    private static void applyTimeouts() {
        TL_DRIVER.get().manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(10))
                .pageLoadTimeout(Duration.ofSeconds(60))
                .scriptTimeout(Duration.ofSeconds(30));
    }

    private static WebDriver createLocalDriver() {
        String browser = System.getProperty("browser",
                prop.getProperty("browser", "chrome")).toLowerCase();
        return switch (browser) {
            case "chrome"  -> new ChromeDriver(chromeOpts());
            case "firefox" -> { WebDriverManager.firefoxdriver().setup();
                yield new FirefoxDriver(); }
            case "edge"    -> { WebDriverManager.edgedriver().setup();
                yield new EdgeDriver(new EdgeOptions()); }
            default        -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browser);
        };
    }

    private static ChromeOptions chromeOpts() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions o = new ChromeOptions();
        o.addArguments("--start-maximized", "--disable-notifications");
        String path = System.getProperty("user.dir") + File.separator + "downloads";
        Map<String,Object> prefs = Map.of(
                "download.default_directory", path,
                "download.prompt_for_download", false);
        o.setExperimentalOption("prefs", prefs);
        return o;
    }

    private static WebDriver createBrowserStackDriver() {
        String user        = prop.getProperty("bs.username");

        String key         = prop.getProperty("bs.key");
        String browser     = System.getProperty("bs.browser",
                prop.getProperty("bs.browser", "Chrome"));
        String browserVer  = System.getProperty("bs.browserVer",
                prop.getProperty("bs.browserVer", "latest"));
        String os          = System.getProperty("bs.os",
                prop.getProperty("bs.os", "Windows"));
        String osVersion   = System.getProperty("bs.osVersion",
                prop.getProperty("bs.osVersion", "11"));
        String device      = System.getProperty("bs.device",
                prop.getProperty("bs.device", ""));

        MutableCapabilities caps = new MutableCapabilities();
        Map<String, Object> bsOpts = new HashMap<>();
        bsOpts.put("userName",     user);
        bsOpts.put("accessKey",    key);
        bsOpts.put("projectName",  "ElPais‑Opinion");
        bsOpts.put("buildName",    "Build‑" + System.currentTimeMillis());
        bsOpts.put("sessionName",  "Thread‑" + Thread.currentThread().getId());
        bsOpts.put("debug",        "true");
        bsOpts.put("idleTimeout",  180);
        bsOpts.put("seleniumVersion", "4.33.0");

        if (device.isBlank()) {
            caps.setCapability("browserName",    browser);
            caps.setCapability("browserVersion", browserVer);
            bsOpts.put("os",        os);
            bsOpts.put("osVersion", osVersion);
        } else {
            caps.setCapability("browserName", "");
            bsOpts.put("deviceName", device);
            bsOpts.put("realMobile", "true");
        }

        caps.setCapability("bstack:options", bsOpts);

        try {
            return new RemoteWebDriver(
                    new URL("https://hub-cloud.browserstack.com/wd/hub"), caps);
        } catch (Exception e) {
            throw new RuntimeException("BrowserStack driver init failed", e);
        }
    }
}
