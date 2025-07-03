# ElPais Test Automation Framework

## Overview
This is a Cucumber BDD-based test automation framework built for validating functionalities of the ElPais website. It integrates with **Selenium WebDriver**, **Extent Reports**, **Apache POI**, and **BrowserStack** to support web UI testing, reporting, cross-browser execution, and data-driven testing.

---

## Features

### 🔧 Functional Highlights

- **Web UI Testing:** Automates ElPais website using Selenium and Cucumber BDD.
- **Cross-Browser Execution:** Supports local and BrowserStack environments.
- **Data-Driven Testing:** Reads test data from Excel sheets.
- **Reporting:** Captures detailed HTML reports using ExtentReports.
- **Screenshot Capture:** Screenshots on failure automatically added to reports.

---

## Project Structure

```

src/
├── main/
│   ├── java/
│   │   ├── config/
│   │   │   ├── BaseObject.java
│   │   │   ├── Setup.java
│   │   │   ├── propertiesUtils.java
│   │   ├── utils/
│   │   │   ├── ExcelReader.java
│   │   │   ├── ExtentReport.java

├── test/
│   ├── java/
│   │   ├── config/
│   │   │   ├── PageObjectManager.java
│   │   ├── feature/
│   │   ├── pages/
│   │   │   ├── ElPais.java
│   │   ├── stepDefinition/
│   │   │   ├── Hooks.java
│   │   │   ├── ElPaisStepDefinition.java
│   │   ├── Testrunner/
│   │   │   ├── TestRunnerElPais.java

src/main/resources/
AutomationReports/
├── extent-reports.html

````

---

## Key Components

### 🛠 Configuration

- **BaseObject.java**: Reusable base functions like `clickElement`, `waitUntilElementVisibility`.
- **Setup.java**:
  - `getDriver()`: Returns initialized WebDriver instance.
  - `initializeDriver()`: Initializes WebDriver based on run mode.
  - `quitDriver()`: Closes the driver session.
- **propertiesUtils.java**: Reads from `config.properties`.

### 📦 Utilities

- **ExcelReader.java**: Reads test data from Excel files.
- **ExtentReport.java**: Manages Extent Report lifecycle.

### 📄 Pages and Step Definitions

- **PageObjectManager.java**: Manages page object initialization.
- **ElPais.java**: Implements Page Object Model for ElPais.
- **Hooks.java**: Handles pre and post-scenario steps.
- **ElPaisStepDefinition.java**: Implements step definitions for features.

### 🧪 Test Runner

- **TestRunnerElPais.java**: Configures Cucumber options and executes features.

---

## How to Run Tests

### ✅ Pre-requisites

- Java 8 or above  
- Maven installed  
- Chrome installed (for local runs)  
- BrowserStack credentials (for remote execution)

### ▶️ Run Locally

```bash
mvn clean test
````

### ▶️ Run on BrowserStack

1. Set the following in `config.properties`:

   ```
   runMode = browserstack
   ```
2. Execute:

   ```bash
   mvn test -Dsurefire.suiteXmlFiles=testing-browserstack.xml
   ```
3. Result - Integrated in 5 devices mode
   * Chrome_Windows
   * Edge_Mac
   * iPhone13_Safari
   * Galaxy_S24
   * Edge_Windows
     
4. Screenshot (Passed - 5/5) 
    ref Number: Build 1751540575892 10# and  Build 1751540575893
    #11
    ![image](https://github.com/user-attachments/assets/129a9cc7-6416-4b5f-920c-5b3774d5f916)
    ![image](https://github.com/user-attachments/assets/9dc08fd2-831e-4a5f-bda1-d191f1788700)
![image](https://github.com/user-attachments/assets/35563854-7c9e-4cee-937c-b7909d5a8e37)





### 📊 View Reports

After test execution:

* Navigate to: `AutomationReports/extent-reports.html`
* Open the file in a browser to view the detailed report (with logs and screenshots)

---

## BrowserStack Integration

* Run cross-browser tests in parallel on BrowserStack's cloud grid.
* Set credentials in `config.properties`:

  ```
  bs.username = <your-username>
  bs.key = <your-access-key>
  ```

---

## Technologies Used

* **Java** – Core programming language
* **Selenium WebDriver** – Browser automation
* **Cucumber BDD** – Behavior-Driven Development
* **Extent Reports** – Rich HTML reporting
* **Apache POI** – Excel handling
* **BrowserStack** – Cloud-based cross-browser execution

---

## Notes

* Excel test data is stored under `resources/`
* HTML test reports are generated after every run
* You can toggle between local and remote (BrowserStack) runs via `runMode`

---

## Sample Step Definitions

```java
@Given("User opens ElPais homepage in Spanish")
public void user_opens_homepage() {
    pageObjectManager.getElPais().navigateToHomePage();
}

@Then("User fetches the first five Opinion articles")
public void fetch_opinion_articles() {
    pageObjectManager.getElPais().scrapeFirstNOpinionArticles(5);
}
```

---

## Screenshots and Reports

📁 Path: `AutomationReports/extent-reports.html`
Contains execution logs, pass/fail status, and screenshots on failure.

---

## License

This project is open for educational and demo purposes.

```

Let me know if you also want:
- Badge examples (e.g., Build Passing, Tested on BrowserStack)
- Sample screenshots inlined like your earlier project
- Table of contents or GitHub-style anchor links
```
