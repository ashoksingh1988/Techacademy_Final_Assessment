# ðŸŽ¯ SauceDemo Automation Framework

Comprehensive test automation suite featuring **4 frameworks** covering web and mobile automation using Java, Python, Selenium, Playwright, and Appium.

---

## ðŸ“‹ Project Overview

This project demonstrates enterprise-level test automation capabilities across multiple technologies and platforms. It includes complete automation frameworks for both web and mobile applications, following industry best practices and design patterns.

**Test Application:** [SauceDemo](https://www.saucedemo.com) - E-commerce web application

---

## ðŸš€ Frameworks Included

| Framework | Technology | Platform | Purpose |
|-----------|------------|----------|---------|
| **Java Selenium** | Java + TestNG + Selenium | Web | Enterprise web automation |
| **Java Appium** | Java + TestNG + Appium | Mobile (Android) | Mobile app automation |
| **Python Selenium** | Python + Pytest + Selenium | Web | Python web automation |
| **Python Playwright** | Python + Pytest + Playwright | Web | Modern web automation |

---

## âœ¨ Key Features

### ðŸ—ï¸ Architecture & Design
- **Page Object Model (POM)** - Maintainable and scalable architecture  
- **Data-Driven Testing** - TestNG XML, pytest fixtures, JSON configs  
- **Modular Framework** - Reusable components and utilities  
- **Parallel Execution** - Run tests faster with parallel capabilities  

### ðŸ“Š Reporting & Logging
- **HTML Reports** - ExtentReports (Java), pytest-html (Python)  
- **Screenshots** - Automatic capture on pass/fail  
- **Detailed Logs** - Comprehensive logging for debugging  
- **Test Metrics** - Pass/fail counts, success rates, execution time  

### ðŸ”„ CI/CD Integration
- **Jenkins Pipeline** - Jenkinsfile included for CI/CD  
- **Git Integration** - Version control ready  
- **Docker Support** - Containerized test execution  
- **Cross-Platform** - Windows, Linux, macOS compatible  

### ðŸ›¡ï¸ Best Practices
- **Clean Code** - Well-structured, readable, maintainable  
- **No Redundancy** - DRY principles followed  
- **Error Handling** - Robust exception management  
- **Wait Strategies** - Smart waiting mechanisms  

---

## ðŸš€ Quick Start

### ðŸ“‹ Prerequisites

**For Java Frameworks:**
- Java JDK 11+
- Maven 3.6+

**For Python Frameworks:**
- Python 3.8+
- pip

**For Mobile Automation:**
- Appium Server
- Android SDK
- ADB (Android Debug Bridge)

### ðŸ’» Installation

```bash
# Clone repository
git clone <repository-url>
cd SauceDemoAutomation

# Install Java dependencies (handled by Maven automatically)
# Install Python dependencies
cd python-selenium-automation
py -m pip install -r requirements.txt

cd ../python-playwright-automation
py -m pip install -r requirements.txt
py -m playwright install chromium
```

### â–¶ï¸ Running Tests

#### ðŸŽ¯ From Project Root (Easiest)

```bash
# Java Selenium (Web)
run-testng.bat

# Java Appium (Mobile - requires Appium server running)
run-java-appium.bat

# Python Selenium (Web)
run-python-selenium.bat

# Python Playwright (Web)
run-python-playwright.bat
```

#### ðŸ”§ Framework-Specific Execution

See [`FRAMEWORK_EXECUTION_GUIDE.md`](FRAMEWORK_EXECUTION_GUIDE.md) for detailed instructions.

---

## ðŸ“Š Framework Comparison

| Feature | Java Selenium | Java Appium | Python Selenium | Python Playwright |
|---------|---------------|-------------|-----------------|-------------------|
| **Language** | Java | Java | Python | Python |
| **Test Runner** | TestNG | TestNG | Pytest | Pytest |
| **Platform** | Web | Mobile (Android) | Web | Web |
| **Reports** | ExtentReports | ExtentReports | pytest-html | pytest-html |
| **Parallel** | TestNG parallel | TestNG parallel | pytest-xdist | pytest-xdist |
| **Speed** | âš¡ Standard | ðŸ“± Device-dependent | âš¡ Standard | ðŸš€ Fast |
| **Auto-wait** | âŒ Manual | âŒ Manual | âŒ Manual | âœ… Built-in |
| **Browsers** | Chrome, Firefox, Edge | N/A | Chrome, Firefox, Edge | Chromium, Firefox, WebKit |

---

## ðŸ“ Project Structure

```
SauceDemoAutomation/
â”‚
â”œâ”€â”€ java-selenium-automation/         # Java Selenium framework
â”‚   â”œâ”€â”€ src/main/java/com/selenium/
â”‚   â”‚   â”œâ”€â”€ core/                     # BaseTest, DriverManager
â”‚   â”‚   â”œâ”€â”€ pages/                    # Page Objects
â”‚   â”‚   â””â”€â”€ utils/                    # Utilities
â”‚   â”œâ”€â”€ src/test/java/com/selenium/tests/
â”‚   â”‚   â””â”€â”€ SauceDemoTests.java
â”‚   â””â”€â”€ reports/                      # Test reports
â”‚
â”œâ”€â”€ java-appium-automation/           # Java Appium framework
â”‚   â”œâ”€â”€ src/main/java/com/appium/
â”‚   â”‚   â”œâ”€â”€ core/                     # BaseTest, DriverFactory
â”‚   â”‚   â”œâ”€â”€ pages/                    # Mobile Page Objects
â”‚   â”‚   â””â”€â”€ utils/                    # Utilities
â”‚   â”œâ”€â”€ src/test/java/com/appium/tests/
â”‚   â”‚   â”œâ”€â”€ CalculatorTests.java
â”‚   â”‚   â”œâ”€â”€ ColorNoteTests.java
â”‚   â”‚   â””â”€â”€ GoogleDocsTests.java
â”‚   â””â”€â”€ reports/                      # Test reports
â”‚
â”œâ”€â”€ python-selenium-automation/       # Python Selenium framework
â”‚   â”œâ”€â”€ pages/                        # Page Objects
â”‚   â”œâ”€â”€ tests/                        # Test files
â”‚   â”œâ”€â”€ conftest.py                   # Pytest fixtures
â”‚   â””â”€â”€ reports/                      # Test reports
â”‚
â”œâ”€â”€ python-playwright-automation/     # Python Playwright framework
â”‚   â”œâ”€â”€ pages/                        # Page Objects
â”‚   â”œâ”€â”€ tests/                        # Test files
â”‚   â”œâ”€â”€ conftest.py                   # Pytest fixtures
â”‚   â””â”€â”€ reports/                      # Test reports
â”‚
â”œâ”€â”€ Jenkinsfile                       # CI/CD pipeline
â”œâ”€â”€ FRAMEWORK_EXECUTION_GUIDE.md      # Detailed execution guide
â”œâ”€â”€ JAVA_SELENIUM_FLOW.md             # Java Selenium flow diagram
â”œâ”€â”€ JAVA_APPIUM_FLOW.md               # Java Appium flow diagram
â”œâ”€â”€ PYTHON_SELENIUM_FLOW.md           # Python Selenium flow diagram
â”œâ”€â”€ PYTHON_PLAYWRIGHT_FLOW.md         # Python Playwright flow diagram
â””â”€â”€ README.md                         # This file
```

---

## ðŸ“š Documentation

| Document | Description |
|----------|-------------|
| [`FRAMEWORK_EXECUTION_GUIDE.md`](FRAMEWORK_EXECUTION_GUIDE.md) | Complete execution instructions for all frameworks |
| [`JAVA_SELENIUM_FLOW.md`](JAVA_SELENIUM_FLOW.md) | Java Selenium framework flow diagram and architecture |
| [`JAVA_APPIUM_FLOW.md`](JAVA_APPIUM_FLOW.md) | Java Appium mobile automation flow and architecture |
| [`PYTHON_SELENIUM_FLOW.md`](PYTHON_SELENIUM_FLOW.md) | Python Selenium framework flow with pytest fixtures |
| [`PYTHON_PLAYWRIGHT_FLOW.md`](PYTHON_PLAYWRIGHT_FLOW.md) | Python Playwright modern automation flow |

---

## ðŸŽ“ Framework Architectures

### ðŸŒ Java Selenium - Web Automation
```
Maven â†’ TestNG â†’ BaseTest â†’ DriverManager â†’ Page Objects â†’ Tests â†’ ExtentReports
```

**Key Components:**
- **BaseTest.java** - Test lifecycle management
- **DriverManager.java** - WebDriver creation and configuration
- **Page Objects** - LoginPage, InventoryPage
- **ReportManager.java** - HTML report generation

[ðŸ“„ View detailed flow](JAVA_SELENIUM_FLOW.md)

---

### ðŸ“± Java Appium - Mobile Automation
```
Maven â†’ TestNG â†’ BaseTest â†’ Appium Server â†’ DriverFactory â†’ Mobile Apps â†’ Tests â†’ ExtentReports
```

**Key Components:**
- **DriverFactory.java** - AppiumDriver creation
- **Appium Server** - Middleware between tests and device
- **ConfigurationManager.java** - Device and app configuration
- **Mobile Page Objects** - CalculatorPage, ColorNotePage

**Apps Tested:**
- Calculator (com.google.android.calculator)
- ColorNote (com.socialnmobile.dictapps.notepad.color.note)
- Files by Google (com.google.android.apps.nbu.files)
- Google Docs (com.google.android.apps.docs.editors.docs)

[ðŸ“„ View detailed flow](JAVA_APPIUM_FLOW.md)

---

### ðŸ Python Selenium - Web Automation
```
Pytest â†’ conftest.py â†’ Fixtures â†’ WebDriver â†’ Page Objects â†’ Tests â†’ pytest-html
```

**Key Components:**
- **conftest.py** - Pytest fixtures and hooks
- **driver fixture** - WebDriver creation and cleanup
- **Page Objects** - LoginPage, InventoryPage, CartPage
- **pytest-html** - HTML report generation

**Key Features:**
- Automatic fixture injection
- No explicit setup/teardown calls
- Screenshots on pass/fail

[ðŸ“„ View detailed flow](PYTHON_SELENIUM_FLOW.md)

---

### âš¡ Python Playwright - Modern Web Automation
```
Pytest â†’ conftest.py â†’ Playwright â†’ Browser Context â†’ Page Objects â†’ Tests â†’ pytest-html
```

**Key Components:**
- **playwright_instance fixture** - Playwright session
- **browser fixture** - Single browser for all tests (fast!)
- **context fixture** - Isolated context per test
- **page fixture** - Page object for interactions

**Key Features:**
- ðŸŽ¯ **Modern API** - Clean and intuitive
- âš¡ **Auto-waiting** - No explicit waits needed
- ðŸ“¸ **Full-page screenshots** - Capture entire page
- ðŸ”„ **Multi-browser** - Chromium, Firefox, WebKit
- ðŸš€ **Fast** - Browser reused between tests

[ðŸ“„ View detailed flow](PYTHON_PLAYWRIGHT_FLOW.md)

---

## ðŸ§ª Test Coverage

### ðŸŒ Web Tests (Java Selenium & Python Selenium/Playwright)

| Test Case | Description |
|-----------|-------------|
| Login - Valid Credentials | Successful login with standard user |
| Login - Invalid Credentials | Error message for wrong credentials |
| Login - Locked User | Proper error for locked out user |
| Website Title | Validate page title is correct |
| Product Count | Verify correct number of products |
| Add Item to Cart | Add product to shopping cart |
| Remove Item from Cart | Remove product from cart |
| View Cart Page | Navigate to cart and verify items |
| Logout | Successfully logout from application |

---

### ðŸ“± Mobile Tests (Java Appium)

| App | Test Cases |
|-----|------------|
| **Calculator** | Page load, Addition, Subtraction, Clear functionality |
| **ColorNote** | Create note, View notes, Delete note |
| **Files** | Browse folders, File operations |
| **Google Docs** | Document operations, Text editing |

---

## ðŸ“Š Test Reports

### ðŸ“„ Report Locations

```
java-selenium-automation/reports/Selenium_Web_Automation_Report_<timestamp>.html
java-appium-automation/reports/Appium_Android_Automation_Report_<timestamp>.html
python-selenium-automation/reports/report.html
python-playwright-automation/reports/report.html
```

### âœ… Report Features

- **Test Results** - Pass/fail status for each test  
- **Screenshots** - Visual evidence of test execution  
- **Logs** - Detailed step-by-step logs  
- **Execution Time** - Duration of each test  
- **Environment Info** - Browser, OS, versions  
- **Statistics** - Total tests, pass rate, etc.  

---

## ðŸ”§ Configuration

### â˜• Java Frameworks

**Maven POM:**
- Dependencies: Selenium, Appium, TestNG, ExtentReports
- Plugins: maven-compiler-plugin, maven-surefire-plugin

**TestNG XML:**
- Test suites: smoke-tests.xml, regression-tests.xml
- Parameters: browser, credentials, app configs

**Properties:**
- application.properties: timeouts, URLs, settings

---

### ðŸ Python Frameworks

**requirements.txt:**
- Dependencies: selenium, pytest, pytest-html, webdriver-manager, playwright, pytest-xdist

**pytest.ini:**
- Markers: smoke, regression
- Test discovery patterns
- Report configuration

**conftest.py:**
- Fixtures: driver, browser, page
- Hooks: screenshot capture, reporting

---

## ðŸŒ Browser Support

| Framework | Browsers |
|-----------|----------|
| Java Selenium | Chrome, Firefox, Edge |
| Python Selenium | Chrome, Firefox, Edge |
| Python Playwright | Chromium, Firefox, WebKit (Safari) |

**Headless Mode:** Supported in all frameworks

---

## ðŸ“± Mobile Support

| Framework | Platform | Requirements |
|-----------|----------|--------------|
| Java Appium | Android | Appium Server, Android SDK, ADB |

**Tested On:**
- Android 11+
- Physical devices and emulators

---

## ðŸ”„ CI/CD Integration

### ðŸ”§ Jenkins

```bash
# Jenkinsfile included
# Supports all frameworks
# Automated test execution
# Report archiving
```

**Jenkins Pipeline Stages:**
1. Checkout code
2. Install dependencies
3. Run tests
4. Generate reports
5. Archive artifacts

---

## ðŸŽ¯ Design Patterns

| Pattern | Usage |
|---------|-------|
| **Page Object Model** | All frameworks |
| **Factory Pattern** | Driver creation (DriverManager, DriverFactory) |
| **Singleton Pattern** | ConfigurationManager |
| **Builder Pattern** | Test data builders |

---

## ðŸ›¡ï¸ Best Practices Implemented

### ðŸ’» Code Quality
- Clean, readable code
- Meaningful naming conventions
- DRY principle (Don't Repeat Yourself)
- SOLID principles

### ðŸ§ª Test Design
- Independent tests (no dependencies)
- Deterministic (consistent results)
- Fast execution
- Meaningful assertions

### ðŸ—ï¸ Maintainability
- Page Object separation
- Centralized configuration
- Reusable utilities
- Comprehensive documentation

---

## ðŸ” Troubleshooting

### â— Common Issues

**Issue:** Browser/Driver not found  
**Solution:** WebDriverManager handles this automatically. Ensure internet connection.

**Issue:** Python module not found  
**Solution:** Run `py -m pip install -r requirements.txt`

**Issue:** Appium tests fail to connect  
**Solution:** Ensure Appium server is running on 127.0.0.1:4723

**Issue:** Tests fail intermittently  
**Solution:** Check internet connectivity, increase wait timeouts

---

## ðŸ“ˆ Future Enhancements

- [ ] Cloud device farm integration (Sauce Labs, BrowserStack)
- [ ] Visual regression testing
- [ ] Performance testing integration
- [ ] API testing framework
- [ ] Email testing capabilities
- [ ] Database validation

---

## ðŸ‘¥ Contributing

This is a demonstration project. For improvements or suggestions:

1. Review code structure
2. Follow existing patterns
3. Ensure all tests pass
4. Update documentation

---

## ðŸ“„ License

This project is for educational and demonstration purposes.

---

## ðŸ™ Acknowledgments

**Total Test Cases:** 25+
- 9 Web test cases (across 3 web frameworks)
- 16 Mobile test cases (across 4 apps)

Built with â¤ï¸ using industry-standard tools and best practices.