# Framework Execution Guide

Complete execution guide for all automation frameworks in the SauceDemo Automation project.

---

## 📋 Table of Contents

1. [Java Selenium Automation](#1-java-selenium-automation)
2. [Java Appium Automation](#2-java-appium-automation)
3. [Python Selenium Automation](#3-python-selenium-automation)
4. [Python Playwright Automation](#4-python-playwright-automation)

---

## 1. Java Selenium Automation

### 📍 Purpose
Web automation testing for SauceDemo website using Java, Selenium WebDriver, and TestNG.

### 🔧 Prerequisites
- Java JDK 11 or higher
- Maven
- Chrome/Firefox/Edge browser

### 🚀 Execution Methods

#### **Method 1: Using Root Batch File (Recommended)**
```bash
# From project root directory
run-testng.bat
```

#### **Method 2: Using Framework Batch File**
```bash
# Navigate to framework directory
cd java-selenium-automation

# Run smoke tests
scripts\run-tests.bat smoke

# Run regression tests
scripts\run-tests.bat regression
```

#### **Method 3: Using Maven**
```bash
cd java-selenium-automation

# Run smoke tests
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/selenium-smoke-tests.xml

# Run regression tests
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/selenium-regression-tests.xml
```

### 📊 Test Reports
- Location: `java-selenium-automation/reports/`
- Format: `Selenium_Web_Automation_Report_<timestamp>.html`

### ✅ What Tests Cover
- Login functionality (valid/invalid credentials)
- Product inventory validation
- Shopping cart operations
- Logout functionality

---

## 2. Java Appium Automation

### 📍 Purpose
Mobile automation testing for Android apps using Java, Appium, and TestNG.

### 🔧 Prerequisites
- Java JDK 11 or higher
- Maven
- Appium Server (must be running)
- Android Device or Emulator
- ADB (Android Debug Bridge)

### 🚀 Execution Methods

#### **Method 1: Using Root Batch File (Recommended)**
```bash
# From project root directory

# Start Appium server first (in separate terminal)
appium

# Run tests
run-java-appium.bat
```

#### **Method 2: Using Framework Batch File**
```bash
# Navigate to framework directory
cd java-appium-automation

# Start Appium server (separate terminal)
appium

# Verify device connected
adb devices

# Run smoke tests
scripts\run-tests.bat smoke

# Run regression tests
scripts\run-tests.bat regression

# Run parallel tests
scripts\run-tests.bat parallel
```

#### **Method 3: Using Maven**
```bash
cd java-appium-automation

# Ensure Appium is running
appium

# Run smoke tests
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/smoke-tests.xml

# Run regression tests
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/regression-tests.xml

# Run parallel tests
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/parallel-tests.xml
```

### 📊 Test Reports
- Location: `java-appium-automation/reports/`
- Format: `Appium_Android_Automation_Report_<timestamp>.html`

### ✅ What Tests Cover
- **Calculator App**: Basic calculations, clear functionality
- **ColorNote App**: Note creation, editing, deletion
- **Files App**: File browsing, operations
- **Google Docs App**: Document operations

---

## 3. Python Selenium Automation

### 📍 Purpose
Web automation testing for SauceDemo website using Python, Selenium, and Pytest.

### 🔧 Prerequisites
- Python 3.8 or higher
- pip (Python package manager)

### 🚀 Execution Methods

#### **Method 1: Using Root Batch File (Recommended)**
```bash
# From project root directory
run-python-selenium.bat
```

#### **Method 2: Using Framework Batch File**
```bash
# Navigate to framework directory
cd python-selenium-automation

# Run all tests
run_tests.bat

# Or use pytest directly
py -m pytest tests/ -v
```

#### **Method 3: Using Pytest with Markers**
```bash
cd python-selenium-automation

# Run smoke tests
py -m pytest tests/ -m smoke -v

# Run regression tests
py -m pytest tests/ -m regression -v

# Run specific test
py -m pytest tests/test_saucedemo_comprehensive.py::TestSauceDemoComprehensive::test_verify_website_title -v
```

### 📊 Test Reports
- Location: `python-selenium-automation/reports/`
- Format: `report.html`
- Screenshots: `reports/screenshots/success/` and `reports/screenshots/failure/`

### ✅ What Tests Cover
- Website title verification
- Login with valid/invalid credentials
- Add items to cart
- Remove items from cart
- Logout functionality

---

## 4. Python Playwright Automation

### 📍 Purpose
Modern web automation testing using Python, Playwright, and Pytest.

### 🔧 Prerequisites
- Python 3.8 or higher
- pip (Python package manager)
- Playwright browsers (auto-installed)

### 🚀 Execution Methods

#### **Method 1: Using Root Batch File (Recommended)**
```bash
# From project root directory
run-python-playwright.bat
```

#### **Method 2: Using Framework Batch File**
```bash
# Navigate to framework directory
cd python-playwright-automation

# Run all tests
run_tests.bat

# Run smoke tests
run_tests.bat smoke

# Run regression tests
run_tests.bat regression

# Run in parallel
run_tests.bat parallel
```

#### **Method 3: Using Pytest**
```bash
cd python-playwright-automation

# Run all tests
py -m pytest tests/ -v

# Run smoke tests
py -m pytest tests/ -m smoke -v

# Run regression tests
py -m pytest tests/ -m regression -v

# Run in parallel
py -m pytest tests/ -n auto -v
```

### 📊 Test Reports
- Location: `python-playwright-automation/reports/`
- Format: `report.html`
- Screenshots: `reports/screenshots/success/` and `reports/screenshots/failure/`

### ✅ What Tests Cover
- Website title verification
- Login (valid/invalid/locked user)
- Add/remove items to cart
- View cart page
- Product count validation
- Logout functionality

---

## 🎯 Quick Reference Table

| Framework | Command | Test Types | Report Location |
|-----------|---------|------------|-----------------|
| **Java Selenium** | `run-testng.bat` | smoke, regression | `java-selenium-automation/reports/` |
| **Java Appium** | `run-java-appium.bat` | smoke, regression, parallel | `java-appium-automation/reports/` |
| **Python Selenium** | `run-python-selenium.bat` | smoke, regression | `python-selenium-automation/reports/` |
| **Python Playwright** | `run-python-playwright.bat` | smoke, regression, parallel | `python-playwright-automation/reports/` |

---

## 🔍 Environment Variables

### Java Selenium
```bash
# Browser selection (chrome, firefox, edge)
-Dbrowser=chrome

# Headless mode (true, false)
-Dheadless=false
```

### Java Appium
```bash
# Device name
-DdeviceName=Android_Device

# Platform version
-DplatformVersion=11

# App package
-DappPackage=com.google.android.calculator
```

### Python Selenium
```bash
# Browser selection (chrome, firefox, edge)
set BROWSER=chrome

# Headless mode (true, false)
set HEADLESS=false
```

### Python Playwright
```bash
# Browser selection (chromium, firefox, webkit)
set BROWSER=chromium

# Headless mode (true, false)
set HEADLESS=false
```

---

## 🐛 Troubleshooting

### Java Frameworks
**Issue:** `Command not found: mvn`
- **Solution:** Install Maven and add to PATH

**Issue:** Browser driver not found
- **Solution:** WebDriverManager handles this automatically

### Python Frameworks
**Issue:** `Command not found: py`
- **Solution:** Install Python and add to PATH

**Issue:** Module not found
- **Solution:** Run `py -m pip install -r requirements.txt`

### Appium Framework
**Issue:** Appium server not running
- **Solution:** Start Appium: `appium`

**Issue:** No devices found
- **Solution:** Check ADB: `adb devices`

### General
**Issue:** Tests fail intermittently
- **Solution:** Check internet connection, verify website is accessible

**Issue:** Reports not generated
- **Solution:** Check reports directory permissions

---

## 📝 Best Practices

1. **Always run from project root** using provided batch files
2. **Check prerequisites** before running tests
3. **For Appium tests**, ensure Appium server is running and device is connected
4. **Review HTML reports** after test execution
5. **Check screenshots** for failed tests to debug issues
6. **Use smoke tests** for quick validation
7. **Use regression tests** for comprehensive coverage

---

## 🎓 For Presentation/Demo

### **Recommended Demo Order:**
1. **Python Playwright** - Fastest, modern approach (30 seconds)
2. **Python Selenium** - Classic Python automation (40 seconds)
3. **Java Selenium** - Enterprise Java automation (45 seconds)
4. **Java Appium** - Mobile automation (if device available) (60 seconds)

### **Key Points to Mention:**
- ✅ Multiple frameworks covering web and mobile
- ✅ Page Object Model design pattern
- ✅ Comprehensive HTML reports with screenshots
- ✅ Support for parallel execution
- ✅ CI/CD ready (Jenkins integration)
- ✅ Clean, maintainable, standardized code

---

**Last Updated:** 2025-11-28  
**Version:** 2.0
