# Java Selenium Framework - Flow of Execution

Complete flow diagram showing how Java Selenium framework executes from start to finish.

---

## 🎯 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    JAVA SELENIUM FRAMEWORK                       │
│                    (Web Automation Testing)                      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │  Maven + TestNG  │
                    └─────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │   BaseTest.java │
                    │  (Setup/Teardown)│
                    └─────────────────┘
                              │
                ┌─────────────┼─────────────┐
                ▼             ▼             ▼
        ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
        │DriverManager │ │Page Objects  │ │ReportManager │
        └──────────────┘ └──────────────┘ └──────────────┘
                │             │             │
                └─────────────┼─────────────┘
                              ▼
                    ┌─────────────────┐
                    │ Test Execution  │
                    │SauceDemoTests   │
                    └─────────────────┘
```

---

## 📊 Detailed Execution Flow

### **Step 1: Test Initiation**

```
USER RUNS COMMAND
       │
       ▼
run-testng.bat  OR  mvn clean test
       │
       ▼
Maven reads pom.xml
       │
       ├─→ Downloads dependencies (Selenium, TestNG, etc.)
       ├─→ Compiles Java source code
       └─→ Finds TestNG XML file
       │
       ▼
TestNG reads XML file (selenium-smoke-tests.xml)
       │
       ├─→ Gets test parameters (browser, username, password)
       ├─→ Identifies test classes (SauceDemoTests.java)
       └─→ Determines execution order
```

---

### **Step 2: Test Suite Setup (@BeforeSuite)**

```
BaseTest.java → @BeforeSuite
       │
       ├─→ Initialize Logging System
       │   └─→ Set log level, format, output destination
       │
       ├─→ Initialize ReportManager
       │   └─→ Create ExtentReports instance
       │   └─→ Set report configuration
       │
       └─→ Setup Screenshot Utility
           └─→ Create screenshots directory
```

**Key Classes Involved:**
- `BaseTest.java` - Main orchestrator
- `ReportManager.java` - Report setup
- `ConfigurationManager.java` - Read properties

---

### **Step 3: Test Method Setup (@BeforeMethod)**

```
BaseTest.java → @BeforeMethod (runs before EACH test)
       │
       ▼
┌──────────────────────────────────────────────────────────┐
│              DriverManager.initializeDriver()             │
└──────────────────────────────────────────────────────────┘
       │
       ├─→ Read browser type from TestNG XML (chrome/firefox/edge)
       ├─→ Read headless mode setting
       │
       ▼
┌──────────────────────────────────────────────────────────┐
│         WebDriverManager (Auto Driver Download)           │
└──────────────────────────────────────────────────────────┘
       │
       ├─→ Check if correct driver version installed
       ├─→ Download/update ChromeDriver if needed
       └─→ Return driver binary path
       │
       ▼
┌──────────────────────────────────────────────────────────┐
│              Create WebDriver Instance                    │
└──────────────────────────────────────────────────────────┘
       │
       ├─→ Set ChromeOptions (maximize, disable notifications)
       ├─→ Launch Chrome browser
       └─→ Set implicit wait (10 seconds)
       │
       ▼
driver.get("https://www.saucedemo.com")
       │
       └─→ Browser navigates to SauceDemo website
```

**Key Classes Involved:**
- `BaseTest.java` - Calls driver initialization
- `DriverManager.java` - Creates and configures WebDriver
- `WebDriverManager` library - Handles driver binary

---

### **Step 4: Test Execution**

```
SauceDemoTests.java → @Test methods execute
       │
       ▼
┌────────────────────────────────────────────────────────────┐
│  Test 1: verifyWebsiteTitle()                               │
│  Priority: 1                                                │
└────────────────────────────────────────────────────────────┘
       │
       ├─→ Get username from DataReader.getUsername(context)
       ├─→ Get password from DataReader.getPassword(context)
       │
       ▼
┌────────────────────────────────────────────────────────────┐
│         LoginPage loginPage = new LoginPage(driver)         │
└────────────────────────────────────────────────────────────┘
       │
       ├─→ LoginPage constructor initializes elements
       │   └─→ PageFactory.initElements(driver, this)
       │       └─→ Finds all @FindBy annotated elements
       │
       ▼
Assert.assertEquals("Swag Labs", driver.getTitle())
       │
       ├─→ If PASS: Mark test as passed ✓
       └─→ If FAIL: Mark test as failed ✗, take screenshot
```

---

### **Step 5: Page Object Interaction**

```
┌──────────────────────────────────────────────────────────────┐
│                    LoginPage.java                             │
└──────────────────────────────────────────────────────────────┘
       │
       ▼
Element Locators (defined using @FindBy)
       │
       ├─→ @FindBy(id = "user-name") → usernameField
       ├─→ @FindBy(id = "password") → passwordField
       └─→ @FindBy(id = "login-button") → loginButton
       │
       ▼
loginPage.login(username, password)
       │
       ├─→ usernameField.clear()
       ├─→ usernameField.sendKeys("standard_user")
       ├─→ passwordField.clear()
       ├─→ passwordField.sendKeys("secret_sauce")
       └─→ loginButton.click()
       │
       ▼
Wait for page load (explicit wait)
       │
       └─→ WebDriverWait until URL contains "inventory"
```

**Data Flow:**
```
TestNG XML → DataReader.java → Test Method → Page Object → WebDriver → Browser
```

---

### **Step 6: Test Verification**

```
After Page Action
       │
       ▼
InventoryPage inventoryPage = new InventoryPage(driver)
       │
       ├─→ Check if on correct page
       └─→ inventoryPage.isInventoryPageDisplayed()
       │
       ▼
Assert.assertTrue(condition, "Error message")
       │
       ├─→ If TRUE: Test continues ✓
       └─→ If FALSE: Test fails, throw AssertionError ✗
```

---

### **Step 7: Test Cleanup (@AfterMethod)**

```
BaseTest.java → @AfterMethod (runs after EACH test)
       │
       ├─→ Capture screenshot
       │   └─→ ScreenshotUtils.takeScreenshot(driver, testName)
       │       └─→ Save to: reports/screenshots/
       │
       ├─→ Log test result
       │   └─→ ReportManager.logTest(testName, status)
       │
       └─→ Close browser
           └─→ driver.quit()
```

**Screenshot Logic:**
```
if (testResult.PASS) {
    save to: screenshots/success/
} else {
    save to: screenshots/failure/
}
```

---

### **Step 8: Next Test Execution**

```
@BeforeMethod → Open fresh browser
       │
       ▼
@Test → Run next test method
       │
       ▼
@AfterMethod → Close browser
       │
       └─→ Repeat for all test methods
```

**Important:** Each test gets a **fresh browser instance** for isolation.

---

### **Step 9: Test Suite Cleanup (@AfterSuite)**

```
BaseTest.java → @AfterSuite (runs once after ALL tests)
       │
       ├─→ Flush ExtentReports
       │   └─→ ReportManager.flushReports()
       │       └─→ Write all test data to HTML file
       │       └─→ Generate: Selenium_Web_Automation_Report_<timestamp>.html
       │
       ├─→ Close any remaining browser instances
       │   └─→ Check if driver != null, then quit()
       │
       └─→ Print execution summary
           └─→ "Tests run: 5, Passed: 4, Failed: 1"
```

---

## 🔄 Complete Flow Diagram (Start to Finish)

```
┌─────────────────────────────────────────────────────────────┐
│ Step 1: User runs command → run-testng.bat                  │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 2: Maven builds project, TestNG loads XML config       │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 3: @BeforeSuite → Setup logging, reports, config       │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 4: @BeforeMethod → DriverManager creates browser       │
│         driver.get("https://www.saucedemo.com")              │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 5: @Test(priority=1) → verifyWebsiteTitle()            │
│         LoginPage → Check title → Assert                    │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 6: @AfterMethod → Screenshot, log result, close browser│
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 7: Repeat Steps 4-6 for remaining tests                │
│         @Test(priority=2) → loginWithValidCredentials()     │
│         @Test(priority=3) → loginWithInvalidCredentials()   │
│         ... (more tests)                                     │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 8: @AfterSuite → Generate HTML report, print summary   │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 9: DONE! Report saved in reports/ folder               │
└─────────────────────────────────────────────────────────────┘
```

---

## 🗂️ File Interaction Map

```
User Command
    │
    ├─→ run-testng.bat
    │       │
    │       └─→ java-selenium-automation/pom.xml
    │               │
    │               └─→ src/test/resources/suites/selenium-smoke-tests.xml
    │                       │
    │                       └─→ src/test/java/com/selenium/tests/SauceDemoTests.java
    │                               │
    │                               ├─→ src/main/java/com/selenium/core/BaseTest.java
    │                               │       │
    │                               │       ├─→ src/main/java/com/selenium/core/DriverManager.java
    │                               │       └─→ src/main/java/com/selenium/utils/ReportManager.java
    │                               │
    │                               └─→ src/main/java/com/selenium/pages/LoginPage.java
    │                                       │
    │                                       └─→ src/main/java/com/selenium/pages/InventoryPage.java
    │
    └─→ Output
            ├─→ reports/Selenium_Web_Automation_Report_*.html
            └─→ reports/screenshots/success/*.png
            └─→ reports/screenshots/failure/*.png
```

---

## 📚 Key Classes and Their Roles

### **1. BaseTest.java**
- **Role:** Foundation class, test lifecycle management
- **Methods:**
  - `@BeforeSuite` → One-time setup
  - `@BeforeMethod` → Create browser before each test
  - `@AfterMethod` → Close browser after each test
  - `@AfterSuite` → Final cleanup and reporting

### **2. DriverManager.java**
- **Role:** Browser creation and configuration
- **Methods:**
  - `initializeDriver(browser, headless)` → Create WebDriver
  - `getDriver()` → Return current driver instance
  - `quitDriver()` → Close and cleanup driver

### **3. LoginPage.java**
- **Role:** Login page interactions
- **Elements:**
  - `usernameField`, `passwordField`, `loginButton`
- **Methods:**
  - `login(username, password)` → Perform login
  - `isLoginPageDisplayed()` → Verify on login page
  - `getErrorMessage()` → Get error text

### **4. InventoryPage.java**
- **Role:** Product page interactions
- **Elements:**
  - `inventoryList`, `addToCartButton`, `cartBadge`
- **Methods:**
  - `isInventoryPageDisplayed()` → Verify on products page
  - `addItemToCart()` → Add product to cart

### **5. ReportManager.java**
- **Role:** HTML report generation
- **Methods:**
  - `startTest(testName)` → Begin test in report
  - `logPass(message)` → Log success
  - `logFail(message)` → Log failure
  - `flushReports()` → Save report to file

### **6. DataReader.java**
- **Role:** Read test data from TestNG XML
- **Methods:**
  - `getUsername(context)` → Get username parameter
  - `getPassword(context)` → Get password parameter
  - `getBrowser(context)` → Get browser type

---

## 🎯 Data Flow

```
TestNG XML (selenium-smoke-tests.xml)
    │
    ├─→ <parameter name="username" value="standard_user"/>
    ├─→ <parameter name="password" value="secret_sauce"/>
    └─→ <parameter name="browser" value="chrome"/>
                │
                ▼
        DataReader.java
                │
                ▼
        SauceDemoTests.java
                │
                ▼
        Page Objects (LoginPage)
                │
                ▼
        WebDriver Actions
                │
                ▼
        Browser Automation
```

---

## ⚙️ Configuration Files

### **pom.xml**
- Maven project configuration
- Dependencies: Selenium, TestNG, WebDriverManager, ExtentReports
- Build plugins: maven-compiler-plugin, maven-surefire-plugin

### **selenium-smoke-tests.xml**
- TestNG suite configuration
- Test parameters (browser, credentials, URLs)
- Test class references
- Parallel execution settings

### **application.properties**
- Timeout values
- Wait durations
- Screenshot settings
- Log levels

---

**Last Updated:** 2025-11-28  
**Framework Version:** 1.0
