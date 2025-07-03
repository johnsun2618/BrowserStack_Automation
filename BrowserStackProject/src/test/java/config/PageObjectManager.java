package config;
import org.openqa.selenium.WebDriver;
import pages.ElPaisPage;

public class PageObjectManager {
    private WebDriver driver;
    private ElPaisPage elPaisPage;

    public PageObjectManager(WebDriver driver) {
        this.driver = driver;
    }

    public ElPaisPage getElPaisPage() {
        return (elPaisPage == null) ? elPaisPage = new ElPaisPage(driver) : elPaisPage;
    }
}

