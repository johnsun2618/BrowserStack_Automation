package stepDefintion;

import config.PageObjectManager;
import config.Setup;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import pages.ArticleData;
import utils.ExtentReport;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ElPaisStepDefinition {
    private WebDriver driver = Setup.getDriver();
    private PageObjectManager pageObjectManager = new PageObjectManager(driver);
    private List<ArticleData> articles;
    private List<String> translatedTitles;


    @Given("User opens ElPais homepage in Spanish")
    public void user_opens_ElPais_homepage_in_Spanish() {
        pageObjectManager.getElPaisPage().getElPaisHomePage();
        ExtentReport.logStep("Navigated to ElPaís Spanish homepage",true);
    }

    @When("User navigates to the Opinion section")
    public void user_navigates_to_the_Opinion_section() throws InterruptedException {
        pageObjectManager.getElPaisPage().gotoOpinionTab();
        ExtentReport.logStep("Clicked on Opinión Tab",true);
    }

    @When("User fetches the first five Opinion articles")
    public void user_fetches_the_first_five_opinion_articles() throws InterruptedException {
        articles = pageObjectManager.getElPaisPage().scrapeFirstNOpinionArticles(5);
        String titlesCsv = articles.stream().map(a -> a.title).collect(java.util.stream.Collectors.joining(" | "));
        ExtentReport.logStep("Fetched 5 opinion articles: " + titlesCsv, true);
    }

    @Then("User prints each article's Spanish title and content and download images")
    public void user_prints_each_article_spanish_title_and_content() {
        for (ArticleData article : articles) {
            System.out.println("Title: " + article.title);
            System.out.println("Content: " + article.body.substring(0, Math.min(200, article.body.length())) + "…");
            if (article.imagePath != null)
                System.out.println("Image present → " + article.imagePath);
        }
        ExtentReport.logStep("Printed article titles, content and downloaded images (if available)", true);
    }

    @When("User translates all article titles to English")
    public void user_translates_all_article_titles_to_English() {
        translatedTitles = pageObjectManager.getElPaisPage().translateTitlesToEnglish(articles);
        String joined = String.join(" | ", translatedTitles);
        ExtentReport.logStep("Translated titles → English: " + joined, true);
    }

    @And("User prints every word that appears more than twice across all translated titles")
    public void user_prints_repeated_words() {
        Map<String,Integer> repeats = pageObjectManager.getElPaisPage().repeatedWords(translatedTitles, 2); // changed from 3 to 2

        if (repeats.isEmpty()) {
            ExtentReport.logStep("No repeated words found.", true);
        } else {
            repeats.forEach((w,c) -> System.out.println(w + " → " + c));
            String logText = repeats.entrySet().stream()
                    .map(e -> e.getKey() + " → " + e.getValue())
                    .collect(Collectors.joining("\n"));
            ExtentReport.logStep("Repeated words:<br><pre>" + logText + "</pre>", true);
//            driver.quit();
        }
    }


}
