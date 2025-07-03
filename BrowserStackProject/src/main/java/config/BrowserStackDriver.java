package config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class BrowserStackDriver {
    public static WebDriver createDriver(String os, String browser, String browserVersion, String device) {
        try {
            DesiredCapabilities caps = new DesiredCapabilities();
            if (device == null) { // Desktop
                caps.setCapability("os", os);
                caps.setCapability("browser", browser);
                caps.setCapability("browser_version", browserVersion);
                caps.setCapability("name", "ElPais Test - " + browser);
            } else { // Mobile
                caps.setCapability("device", device);
                caps.setCapability("realMobile", "true");
                caps.setCapability("name", "ElPais Test - " + device);
            }

            caps.setCapability("browserstack.user", "anmoldas_l4CAxk");
            caps.setCapability("browserstack.key", "piSStwKiqzBVDwrthu2R");

            return new RemoteWebDriver(new URL("https://hub-cloud.browserstack.com/wd/hub"), caps);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

