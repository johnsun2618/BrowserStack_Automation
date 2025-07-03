package pages;

import static org.testng.Assert.assertEquals;

import java.net.URL;

import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.openqa.selenium.JavascriptExecutor;
import com.google.cloud.translate.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import config.BaseObject;
import utils.ExcelReader;
import utils.propertiesUtils;

public class ElPaisPage extends BaseObject {

	WebDriver driver;
	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

	@FindBy(xpath = "//*[contains(text(),'Accept')]")
	private WebElement cookieBanner;

	@FindBy(xpath = "(//li[@id='edition_head'])[1]/a/child::span")
	private WebElement spanishLang;

	@FindBy(xpath = "(//a[contains(text(),'Opinión')])[1]")
	private WebElement opnButton;

	@FindBy(xpath = "//h2[contains(@class,'c_t ')]/a")
	private WebElement opinionAnchors;

//	@FindBy(xpath = "//*[@id='main-content']/header/div/h1")
//	private WebElement articleTitle;
	@FindBy(xpath = "//div[contains(@class,'a_c') and contains(@class,'clearfix')]")
	private WebElement articleBody;
	private static final By ARTICLE_TITLE = By.cssSelector("#main-content header h1, h1");
	private static final By ARTICLE_BODY  = By.xpath("//div[contains(@class,'a_c') and contains(@class,'clearfix')]");

	@FindBy(css = "figure img, div.c_a img")
	private WebElement coverImageSel;

	@FindBy(xpath = "//button[@class='btn btn-h btn-i ep_m']")
	private WebElement hambburger;

	public ElPaisPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		PageFactory.initElements(driver, this);
	}

	public void getElPaisHomePage() {
		System.out.println("Entering getElPaisHomePage()...");
	    driver.get(propertiesUtils.getProperty("url"));
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

			WebElement acceptBtn = wait.until(ExpectedConditions.elementToBeClickable(cookieBanner));
			acceptBtn.click();
		} catch (TimeoutException e) {
			System.out.println("No cookie banner displayed.");
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", cookieBanner);

		}

	}


	public void gotoOpinionTab() throws InterruptedException {
		System.out.println("Entering gotoOpinionTab()...");
		Thread.sleep(2000);
		try {
			opnButton.click();  // try regular click
		} catch (Exception e) {
			System.out.println("Standard click failed, using JS click instead.");
			Thread.sleep(2000);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", opnButton);
		}//		wait.until(ExpectedConditions.urlContains("opinion"));

	}
	private String textOfFirst(By... locators) {
		for (By sel : locators) {
			List<WebElement> els = driver.findElements(sel);
			if (!els.isEmpty()) {
				return els.get(0).getText();
			}
		}
		return "";
	}
	@SuppressWarnings("unchecked")
	private List<String> firstNOpinionLinks(int n) {
		String script =
				"return [...new Set(" +
						"  Array.from(document.querySelectorAll('h2.c_t a'))" +
						"       .slice(0, arguments[0])" +
						"       .map(a => a.href)" +
						")];";
		return (List<String>) ((JavascriptExecutor) driver).executeScript(script, n);
	}

	public List<ArticleData> scrapeFirstNOpinionArticles(int n) throws InterruptedException {
		List<String> links = firstNOpinionLinks(n);

		List<ArticleData> out = new ArrayList<>();
		String original = driver.getWindowHandle();
		System.out.println("All Links : "+links);
		for (String href : links) {
			Thread.sleep(2000);
			((JavascriptExecutor) driver).executeScript("window.open(arguments[0], '_blank');", href);
			driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).getLast());
			Thread.sleep(2000);
			String title;
			try {
//				wait.until(ExpectedConditions.presenceOfElementLocated(ARTICLE_TITLE));
				Thread.sleep(4000);

				title = driver.findElement(ARTICLE_TITLE).getText();
			} catch (NoSuchElementException e1) {
				try {
					title = driver.findElement(By.tagName("h1")).getText(); // fallback
				} catch (NoSuchElementException e2) {
					System.out.println("Title not found on article page.");
					title = "Title Not Found";
				}
			}			System.out.println(title+"\n");
//			String body= textOfFirst(By.xpath("//div[contains(@class,'a_c') and contains(@class,'clearfix')]"),By.cssSelector("div.a_c, div.c_a__content"),By.xpath("//main//article//p"),By.xpath("//h1/following-sibling::h2"));
			String body = "";
			try {
				body = articleBody.getText();
			} catch (NoSuchElementException e) {
				body = textOfFirst(
						By.cssSelector("div.a_c, div.c_a__content"),
						By.xpath("//main//article//p"),
						By.xpath("//h1/following-sibling::h2"),
						By.tagName("article")
				);
			}


			String imgPath = saveCoverImageIfPresent(title);
			out.add(new ArticleData(title, body, imgPath));
			((JavascriptExecutor) driver).executeScript("window.close();");

			driver.switchTo().window(original);

		}
		return out;
	}

	private String saveCoverImageIfPresent(String title) {
		try {
			WebElement img = coverImageSel;
			String src = img.getAttribute("src");
			byte[] bytes = new URL(src).openStream().readAllBytes();
			String safe = title.replaceAll("[\\\\/:*?\"<>|]", "_");
			Path out = Paths.get("images", safe + ".jpg");
			Files.createDirectories(out.getParent());
			Files.write(out, bytes);
			return out.toString();
		} catch (Exception e) { return null; }
	}
	public List<String> translateTitlesToEnglish(List<ArticleData> arts) {
		String rapidKey = utils.propertiesUtils.getProperty("RapidAPIKey");
		if (rapidKey.isBlank()) throw new IllegalStateException("RAPIDAPI_KEY system property not set");

		return
				arts.stream().map(a -> {
			try {
				String encoded = java.net.URLEncoder.encode(a.title, java.nio.charset.StandardCharsets.UTF_8);
				io.restassured.response.Response res = io.restassured.RestAssured.given()
						.header("content-type", "application/x-www-form-urlencoded")
						.header("X-RapidAPI-Key", rapidKey)
						.header("X-RapidAPI-Host", "text-translator2.p.rapidapi.com")
						.body("source_language=es&target_language=en&text=" + encoded)
						.post("https://text-translator2.p.rapidapi.com/translate");
				return res.jsonPath().getString("data.translatedText");
			} catch (Exception e) {
				return a.title + " (translation failed)";
			}
		}).collect(java.util.stream.Collectors.toList());
	}
	public Map<String,Integer> repeatedWords(List<String> titles, int minOccurrences) {
		Map<String,Integer> freq = new HashMap<>();
		for (String t : titles) {
			for (String w : t.toLowerCase().split("\\W+")) {
				if (w.length() < 3) continue;
				freq.put(w, freq.getOrDefault(w, 0) + 1);
			}
		}

		return freq.entrySet().stream()
				.filter(e -> e.getValue() >= minOccurrences)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}




