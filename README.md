# SauceDemo Automation Framework

Comprehensive test automation suite featuring **4 frameworks** covering web and mobile automation using Java, Python, Selenium, Playwright, and Appium.

---

## Project Overview

This project demonstrates enterprise-level test automation capabilities across multiple technologies and platforms. It includes complete automation frameworks for both web and mobile applications, following industry best practices and design patterns.

**Test Application:** [SauceDemo](https://www.saucedemo.com) - E-commerce web application

---

## Frameworks Included

| Framework | Technology | Platform | Purpose |
|-----------|------------|----------|---------|
| **Java Selenium** | Java + TestNG + Selenium | Web | Enterprise web automation |
| **Java Appium** | Java + TestNG + Appium | Mobile (Android) | Mobile app automation |
| **Python Selenium** | Python + Pytest + Selenium | Web | Python web automation |
| **Python Playwright** | Python + Pytest + Playwright | Web | Modern web automation |

---

## Key Features

### Architecture & Design
- **Page Object Model (POM)** - Maintainable and scalable architecture  
- **Data-Driven Testing** - TestNG XML, pytest fixtures, JSON configs  
- **Modular Framework** - Reusable components and utilities  
- **Parallel Execution** - Run tests faster with parallel capabilities  

### Reporting & Logging
- **HTML Reports** - ExtentReports (Java), pytest-html (Python)  
- **Screenshots** - Automatic capture on pass/fail  
- **Detailed Logs** - Comprehensive logging for debugging  
- **Test Metrics** - Pass/fail counts, success rates, execution time  

### CI/CD Integration
- **Jenkins Pipeline** - Jenkinsfile included for CI/CD  
- **Git Integration** - Version control ready  
- **Docker Support** - Containerized test execution  
- **Cross-Platform** - Windows, Linux, macOS compatible  

### Best Practices
- **Clean Code** - Well-structured, readable, maintainable  
- **No Redundancy** - DRY principles followed  
- **Error Handling** - Robust exception management  
- **Wait Strategies** - Smart waiting mechanisms  

---

## Quick Start

### Prerequisites

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

### Installation

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

### Running Tests

#### From Project Root (Easiest)

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

#### Framework-Specific Execution

See [`FRAMEWORK_EXECUTION_GUIDE.md`](FRAMEWORK_EXECUTION_GUIDE.md) for detailed instructions.

---

## Framework Comparison

| Feature | Java Selenium | Java Appium | Python Selenium | Python Playwright |
|---------|---------------|-------------|-----------------|-------------------|
| **Language** | Java | Java | Python | Python |
| **Test Runner** | TestNG | TestNG | Pytest | Pytest |
| **Platform** | Web | Mobile (Android) | Web | Web |
| **Reports** | ExtentReports | ExtentReports | pytest-html | pytest-html |
| **Parallel** | TestNG parallel | TestNG parallel | pytest-xdist | pytest-xdist |
| **Speed** | Standard | Device-dependent | Standard | Fast |
| **Auto-wait** | Manual | Manual | Manual | Built-in |
| **Browsers** | Chrome, Firefox, Edge | N/A | Chrome, Firefox, Edge | Chromium, Firefox, WebKit |

---

## Project Structure

```
SauceDemoAutomation/
|
+-- java-selenium-automation/         # Java Selenium framework
|   +-- src/main/java/com/selenium/
|   |   +-- core/                     # BaseTest, DriverManager
|   |   +-- pages/                    # Page Objects
|   |   +-- utils/                    # Utilities
|   +-- src/test/java/com/selenium/tests/
|   |   +-- SauceDemoTests.java
|   +-- reports/                      # Test reports
|
+-- java-appium-automation/           # Java Appium framework
|   +-- src/main/java/com/appium/
|   |   +-- core/                     # BaseTest, DriverFactory
|   |   +-- pages/                    # Mobile Page Objects
|   |   +-- utils/                    # Utilities
|   +-- src/test/java/com/appium/tests/
|   |   +-- CalculatorTests.java
|   |   +-- ColorNoteTests.java
|   |   +-- GoogleDocsTests.java
|   +-- reports/                      # Test reports
|
+-- python-selenium-automation/       # Python Selenium framework
|   +-- pages/                        # Page Objects
|   +-- tests/                        # Test files
|   +-- conftest.py                   # Pytest fixtures
|   +-- reports/                      # Test reports
|
+-- python-playwright-automation/     # Python Playwright framework
|   +-- pages/                        # Page Objects
|   +-- tests/                        # Test files
|   +-- conftest.py                   # Pytest fixtures
|   +-- reports/                      # Test reports
|
+-- Jenkinsfile                       # CI/CD pipeline
+-- FRAMEWORK_EXECUTION_GUIDE.md      # Detailed execution guide
+-- JAVA_SELENIUM_FLOW.md             # Java Selenium flow diagram
+-- JAVA_APPIUM_FLOW.md               # Java Appium flow diagram
+-- PYTHON_SELENIUM_FLOW.md           # Python Selenium flow diagram
+-- PYTHON_PLAYWRIGHT_FLOW.md         # Python Playwright flow diagram
+-- README.md                         # This file
```

---

## Documentation

| Document | Description |
|----------|-------------|
| [`FRAMEWORK_EXECUTION_GUIDE.md`](FRAMEWORK_EXECUTION_GUIDE.md) | Complete execution instructions for all frameworks |
| [`JAVA_SELENIUM_FLOW.md`](JAVA_SELENIUM_FLOW.md) | Java Selenium framework flow diagram and architecture |
| [`JAVA_APPIUM_FLOW.md`](JAVA_APPIUM_FLOW.md) | Java Appium mobile automation flow and architecture |
| [`PYTHON_SELENIUM_FLOW.md`](PYTHON_SELENIUM_FLOW.md) | Python Selenium framework flow with pytest fixtures |
| [`PYTHON_PLAYWRIGHT_FLOW.md`](PYTHON_PLAYWRIGHT_FLOW.md) | Python Playwright modern automation flow |

---

## Framework Architectures

### Java Selenium - Web Automation
```
Maven -> TestNG -> BaseTest -> DriverManager -> Page Objects -> Tests -> ExtentReports
```

**Key Components:**
- **BaseTest.java** - Test lifecycle management
- **DriverManager.java** - WebDriver creation and configuration
- **Page Objects** - LoginPage, InventoryPage
- **ReportManager.java** - HTML report generation

[View detailed flow](JAVA_SELENIUM_FLOW.md)

---

### Java Appium - Mobile Automation
```
Maven -> TestNG -> BaseTest -> Appium Server -> DriverFactory -> Mobile Apps -> Tests -> ExtentReports
```

**Key Components:**
- **DriverFactory.java** - AppiumDriver creation
- **Mobile Page Objects** - CalculatorPage, ColorNotePage
- **ConfigurationManager.java** - Device and app configuration
- **Appium Server** - Middleware between tests and device

**Apps Tested:**
- Calculator (com.google.android.calculator)
- ColorNote (com.socialnmobile.dictapps.notepad.color.note)
- Files by Google (com.google.android.apps.nbu.files)
- Google Docs (com.google.android.apps.docs.editors.docs)

[View detailed flow](JAVA_APPIUM_FLOW.md)

---

### Python Selenium - Web Automation
```
Pytest -> conftest.py -> Fixtures -> WebDriver -> Page Objects -> Tests -> pytest-html
```

**Key Components:**
- **conftest.py** - Pytest fixtures and hooks
- **driver fixture** - WebDriver creation and cleanup
- **Page Objects** - LoginPage, InventoryPage, CartPage
- **pytest-html** - HTML report generation

**Key Features:**
- Automatic fixture injection
- No explicit setup/teardown calls
- Simple assert statements
- Screenshots on pass/fail

[View detailed flow](PYTHON_SELENIUM_FLOW.md)

---

### Python Playwright - Modern Web Automation
```
Pytest -> conftest.py -> Playwright -> Browser Context -> Page Objects -> Tests -> pytest-html
```

**Key Components:**
- **playwright_instance fixture** - Playwright session
- **browser fixture** - Single browser for all tests (fast!)
- **context fixture** - Isolated context per test
- **page fixture** - Page object for interactions

**Key Features:**
- **Auto-waiting** - No explicit waits needed
- **Fast** - Browser reused between tests
- **Multi-browser** - Chromium, Firefox, WebKit
- **Full-page screenshots** - Capture entire page
- **Modern API** - Clean and intuitive

[View detailed flow](PYTHON_PLAYWRIGHT_FLOW.md)

---

## Test Coverage

### Web Tests (Java Selenium & Python Selenium/Playwright)

| Test Case | Description |
|-----------|-------------|
| Verify Website Title | Validate page title is correct |
| Login - Valid Credentials | Successful login with standard user |
| Login - Invalid Credentials | Error message for wrong credentials |
| Login - Locked User | Proper error for locked out user |
| Add Item to Cart | Add product to shopping cart |
| Remove Item from Cart | Remove product from cart |
| View Cart Page | Navigate to cart and verify items |
| Product Count | Verify correct number of products |
| Logout | Successfully logout from application |

### Mobile Tests (Java Appium)

| App | Test Cases |
|-----|------------|
| **Calculator** | Page load, Addition, Subtraction, Clear functionality |
| **ColorNote** | Create note, View notes, Delete note |
| **Files** | Browse folders, File operations |
| **Google Docs** | Document operations, Text editing |

---

## Test Reports

### Report Locations

```
java-selenium-automation/reports/Selenium_Web_Automation_Report_<timestamp>.html
java-appium-automation/reports/Appium_Android_Automation_Report_<timestamp>.html
python-selenium-automation/reports/report.html
python-playwright-automation/reports/report.html
```

### Report Features

- **Test Results** - Pass/fail status for each test  
- **Execution Time** - Duration of each test  
- **Screenshots** - Visual evidence of test execution  
- **Logs** - Detailed step-by-step logs  
- **Environment Info** - Browser, OS, versions  
- **Statistics** - Total tests, pass rate, etc.  

---

## Configuration

### Java Frameworks

**Maven POM:**
- Dependencies: Selenium, Appium, TestNG, ExtentReports
- Plugins: maven-compiler-plugin, maven-surefire-plugin

**TestNG XML:**
- Test suites: smoke-tests.xml, regression-tests.xml
- Parameters: browser, credentials, app configs

**Properties:**
- application.properties: timeouts, URLs, settings

### Python Frameworks

**requirements.txt:**
- selenium, pytest, pytest-html, webdriver-manager
- playwright, pytest-xdist

**pytest.ini:**
- Test discovery patterns
- Markers: smoke, regression
- Report configuration

**conftest.py:**
- Fixtures: driver, browser, page
- Hooks: screenshot capture, reporting

---

## Browser Support

| Framework | Browsers |
|-----------|----------|
| Java Selenium | Chrome, Firefox, Edge |
| Python Selenium | Chrome, Firefox, Edge |
| Python Playwright | Chromium, Firefox, WebKit (Safari) |

**Headless Mode:** Supported in all frameworks

---

## Mobile Support

| Framework | Platform | Requirements |
|-----------|----------|--------------|
| Java Appium | Android | Appium Server, Android SDK, ADB |

**Tested On:**
- Android 11+
- Physical devices and emulators

---

## CI/CD Integration

### Jenkins

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

## Design Patterns

| Pattern | Usage |
|---------|-------|
| **Page Object Model** | All frameworks |
| **Factory Pattern** | Driver creation (DriverManager, DriverFactory) |
| **Singleton Pattern** | ConfigurationManager |
| **Builder Pattern** | Test data builders |

---

## Best Practices Implemented

### Code Quality
- Clean, readable code
- Meaningful naming conventions
- DRY principle (Don't Repeat Yourself)
- SOLID principles

### Test Design
- Independent tests (no dependencies)
- Deterministic (consistent results)
- Fast execution
- Meaningful assertions

### Maintainability
- Page Object separation
- Centralized configuration
- Reusable utilities
- Comprehensive documentation

---

## Troubleshooting

### Common Issues

**Issue:** Browser/Driver not found  
**Solution:** WebDriverManager handles this automatically. Ensure internet connection.

**Issue:** Appium tests fail to connect  
**Solution:** Ensure Appium server is running on 127.0.0.1:4723

**Issue:** Python module not found  
**Solution:** Run `py -m pip install -r requirements.txt`

**Issue:** Tests fail intermittently  
**Solution:** Check internet connectivity, increase wait timeouts

---

## Future Enhancements

- [ ] API testing framework
- [ ] Performance testing integration
- [ ] Visual regression testing
- [ ] Cloud device farm integration (Sauce Labs, BrowserStack)
- [ ] Database validation
- [ ] Email testing capabilities

---

## Contributing

This is a demonstration project. For improvements or suggestions:
1. Review code structure
2. Follow existing patterns
3. Ensure all tests pass
4. Update documentation

---

## License

This project is for educational and demonstration purposes.

---

## Acknowledgments

- **Selenium** - Web automation
- **Appium** - Mobile automation
- **Playwright** - Modern web automation
- **TestNG** - Java testing framework
- **Pytest** - Python testing framework
- **ExtentReports** - Java reporting library
- **pytest-html** - Python HTML reporting

---

## Contact

For questions or demo walkthrough, please reach out to the project maintainer.

---

**Project Status:** Production Ready  
**Last Updated:** 2025-11-28  
**Version:** 2.0  
**Frameworks:** 4 (Java Selenium, Java Appium, Python Selenium, Python Playwright)  
**Total Test Cases:** 25+
