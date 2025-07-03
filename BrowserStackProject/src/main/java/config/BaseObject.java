package config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BaseObject {

        protected static WebDriver driver;

        public static void setup() {
            System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", "src/main/resources/infra-throne-453706-i3-eb50495c07b7.json");
            driver = Setup.getDriver();
        }

        public static void tearDown() {
            Setup.quitDriver();
        }
    public static void waitForElementVisible(WebElement element, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    public static void waitForElementClickable(WebElement element, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    public static void mouseOverElement(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
    }

    public static void scrollToElement(WebElement element) {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'})", element);
    }

    public static void waitForUrlLoaded(String url, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        wait.until(ExpectedConditions.urlContains(url));
    }

    public static  void clickElement(WebElement element) {
        element.click();
    }

    public static void addValue(WebElement element, String text) {
        element.sendKeys(text);
    }

    public static void clearElement(WebElement element) {
        element.clear();
    }

    public static String getElementText(WebElement element) {
        return element.getText();
    }

    public static boolean isElementDisplayed(WebElement element) {
        return element.isDisplayed();
    }

    public static boolean isElementEnabled(WebElement element) {
        return element.isEnabled();
    }

    public static boolean isElementSelected(WebElement element) {
        return element.isSelected();
    }


}

