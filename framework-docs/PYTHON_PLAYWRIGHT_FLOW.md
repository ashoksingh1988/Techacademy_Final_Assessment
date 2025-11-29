# Python Playwright Framework - Flow of Execution

Complete flow diagram showing how Python Playwright framework executes with Pytest.

---

## 🎯 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│              PYTHON PLAYWRIGHT FRAMEWORK                         │
│             (Modern Web Automation - Pytest)                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │     Pytest      │
                    └─────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │  conftest.py    │
                    │   (Fixtures)    │
                    └─────────────────┘
                              │
                ┌─────────────┼─────────────┐
                ▼             ▼             ▼
        ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
        │Playwright    │ │Page Objects  │ │Screenshot    │
        │Fixtures      │ │              │ │              │
        └──────────────┘ └──────────────┘ └──────────────┘
                │             │             │
                └─────────────┼─────────────┘
                              ▼
                    ┌─────────────────┐
                    │ Test Execution  │
                    │test_saucedemo   │
                    └─────────────────┘
```

---

## 📊 Key Differences: Playwright vs Selenium

| Feature | Playwright | Selenium |
|---------|-----------|----------|
| **Driver** | No WebDriver needed | Requires ChromeDriver |
| **Auto-wait** | Built-in smart waiting | Manual waits needed |
| **Speed** | Faster execution | Standard speed |
| **API** | Modern, cleaner | Traditional |
| **Browsers** | Chromium, Firefox, WebKit | Chrome, Firefox, Edge |
| **Setup** | `playwright install` | webdriver-manager |

---

## 📊 Detailed Execution Flow

### **Step 1: Test Initiation**

```
USER RUNS COMMAND
       │
       ▼
run_tests.bat  OR  py -m pytest tests/
       │
       ▼
Pytest discovers tests
       │
       ├─→ Looks for files matching: test_*.py
       ├─→ Looks for classes matching: Test*
       └─→ Looks for methods matching: test_*
       │
       ▼
Found: test_saucedemo_playwright.py
       │
       └─→ Class: TestSauceDemoPlaywright
           └─→ Methods: test_verify_website_title,
                        test_login_with_valid_credentials, etc.
```

---

### **Step 2: Pytest Configuration**

```
pytest.ini loaded
       │
       ├─→ testpaths = tests
       ├─→ python_files = test_*.py
       ├─→ addopts = -v --html=reports/report.html
       ├─→ filterwarnings = ignore GIL warnings
       └─→ markers = smoke, regression, login, cart
       │
       ▼
conftest.py loaded (BEFORE tests run)
       │
       └─→ All fixtures defined here become available to tests
```

---

### **Step 3: Session Fixtures (Run Once)**

```
Session-scoped fixtures run ONCE for entire test session:
       │
       ├─→ @pytest.fixture(scope="session")
       │   def playwright_instance():
       │       with sync_playwright() as playwright:
       │           yield playwright
       │   └─→ Creates Playwright instance (stays alive for all tests)
       │
       ├─→ @pytest.fixture(scope="session")
       │   def browser(playwright_instance):
       │       browser = playwright_instance.chromium.launch(
       │           headless=False,
       │           args=['--start-maximized']
       │       )
       │       yield browser
       │       browser.close()
       │   └─→ Launches browser ONCE, reused by all tests
       │
       ├─→ @pytest.fixture(scope="session")
       │   def base_url():
       │       return "https://www.saucedemo.com"
       │
       └─→ @pytest.fixture(scope="session")
           def test_credentials():
               return {...}
```

**Key Difference:**
- **Selenium:** New browser for each test (slow)
- **Playwright:** One browser, new context per test (fast!)

---

### **Step 4: Function Fixtures (Run Per Test)**

```
Before EACH test method:
       │
       ▼
@pytest.fixture(scope="function")
def context(browser):
    """Create new browser context for each test"""
       │
       ├─→ context = browser.new_context(
       │       no_viewport=True,  ← Allows maximized window
       │       ignore_https_errors=True
       │   )
       │
       ├─→ yield context  ← Give context to test
       │
       └─→ context.close()  ← Cleanup after test
       │
       ▼
@pytest.fixture(scope="function")
def page(context):
    """Create new page for each test"""
       │
       ├─→ page = context.new_page()
       │
       ├─→ yield page  ← Give page to test
       │
       └─→ page.close()  ← Cleanup after test
```

**Playwright Architecture:**
```
Playwright Instance (session)
       │
       └─→ Browser (session) ← One browser process
               │
               ├─→ Context 1 (per test) ← Isolated cookies/storage
               │   └─→ Page 1
               │
               ├─→ Context 2 (per test)
               │   └─→ Page 2
               │
               └─→ Context 3 (per test)
                   └─→ Page 3
```

**Benefits:**
- ✅ **Faster:** No browser restart between tests
- ✅ **Isolated:** Each test has clean context (no cookies/cache)
- ✅ **Parallel-ready:** Multiple contexts can run simultaneously

---

### **Step 5: Test Execution**

```
test_saucedemo_playwright.py
       │
       ▼
class TestSauceDemoPlaywright:
       │
       ▼
def test_verify_website_title(self, page, base_url):
    """Pytest automatically injects page and base_url fixtures"""
       │
       ├─→ Pytest sees 'page' parameter
       │   └─→ Calls page() fixture
       │       └─→ Calls context() fixture
       │           └─→ Calls browser() fixture
       │               └─→ Calls playwright_instance() fixture
       │       └─→ Returns Page object to test
       │
       ├─→ Pytest sees 'base_url' parameter
       │   └─→ Calls base_url() fixture
       │       └─→ Returns "https://www.saucedemo.com"
       │
       ▼
Test method executes:
       │
       ├─→ page.goto(base_url)
       │   └─→ Playwright navigates to URL
       │   └─→ Auto-waits for page load ✨
       │
       ├─→ login_page = LoginPage(page)
       │   └─→ LoginPage constructor receives page
       │       └─→ Stores: self.page = page
       │
       ├─→ assert login_page.is_login_page_displayed()
       │   ├─→ If True: Test PASSES ✓
       │   └─→ If False: Test FAILS ✗
       │
       └─→ print("✓ Website title verified successfully")
```

---

### **Step 6: Page Object Interaction (Playwright Style)**

```
┌──────────────────────────────────────────────────────────────┐
│                    login_page.py                              │
└──────────────────────────────────────────────────────────────┘
       │
       ▼
class LoginPage(BasePage):
    """Login page object"""
    
    # Locators (CSS selectors - simple strings!)
    USERNAME_INPUT = "#user-name"
    PASSWORD_INPUT = "#password"
    LOGIN_BUTTON = "#login-button"
       │
       ▼
def login(self, username, password):
    """Perform login"""
       │
       ├─→ self.fill(self.USERNAME_INPUT, username)
       │   └─→ Playwright auto-waits for element to be ready
       │   └─→ Clears field and types text
       │
       ├─→ self.fill(self.PASSWORD_INPUT, password)
       │   └─→ Auto-waits again
       │
       └─→ self.click(self.LOGIN_BUTTON)
           └─→ Auto-waits for button to be clickable
           └─→ Clicks button
           └─→ Auto-waits for navigation to complete ✨
```

**Playwright Auto-Wait Magic:**
```
page.click("#login-button")
       │
       ├─→ Waits for element to exist
       ├─→ Waits for element to be visible
       ├─→ Waits for element to be enabled
       ├─→ Waits for element to be stable (not animating)
       ├─→ Clicks element
       └─→ Success! ✓

NO explicit waits needed! 🎉
```

**Compare to Selenium:**
```python
# Selenium: Manual waits
WebDriverWait(driver, 10).until(
    EC.element_to_be_clickable((By.ID, "login-button"))
)
driver.find_element(By.ID, "login-button").click()

# Playwright: Just click!
page.click("#login-button")
```

---

### **Step 7: Base Page Methods**

```
┌──────────────────────────────────────────────────────────────┐
│                    base_page.py                               │
└──────────────────────────────────────────────────────────────┘

def click(self, selector: str):
    """Click on an element"""
    self.page.click(selector)
    └─→ Playwright handles all waiting automatically

def fill(self, selector: str, text: str):
    """Fill input field with text"""
    self.page.fill(selector, text)
    └─→ Auto-clears field, auto-waits

def is_visible(self, selector: str) -> bool:
    """Check if element is visible"""
    try:
        self.page.wait_for_selector(selector, state="visible", timeout=5000)
        return True
    except:
        return False
```

---

### **Step 8: Test Result Capture**

```
After EACH test completes:
       │
       ▼
conftest.py → pytest_runtest_makereport() hook
       │
       ├─→ Tracks pass/fail for summary
       │   ├─→ item.report_passed = True/False
       │   └─→ item.report_failed = True/False
       │
       ▼
if report.when == "call":
       │
       ├─→ Get page fixture from test
       │   └─→ page = item.funcargs["page"]
       │
       ▼
Take Screenshot
       │
       ├─→ page.screenshot(
       │       path="reports/screenshots/success/test_name.png",
       │       full_page=True  ← Captures entire page!
       │   )
       │
       ├─→ If PASSED:
       │   └─→ Save to: reports/screenshots/success/
       │
       └─→ If FAILED:
           └─→ Save to: reports/screenshots/failure/
```

**Playwright Screenshot Features:**
- ✅ Full page capture (not just viewport)
- ✅ Fast (no scrolling needed)
- ✅ High quality
- ✅ Optional: Video recording

---

### **Step 9: Fixture Cleanup**

```
After test completes:
       │
       ▼
page fixture cleanup
       │
       └─→ page.close()
           └─→ Closes current page/tab
       │
       ▼
context fixture cleanup
       │
       └─→ context.close()
           └─→ Closes browser context (cookies/storage cleared)
           └─→ Browser process STAYS OPEN ✓
```

**Important:** Browser stays open between tests for speed!

---

### **Step 10: Session Finish**

```
After ALL tests complete:
       │
       ▼
browser fixture cleanup (session scope)
       │
       └─→ browser.close()
           └─→ NOW browser process closes
       │
       ▼
playwright_instance fixture cleanup
       │
       └─→ Playwright shuts down
       │
       ▼
conftest.py → pytest_sessionfinish() hook
       │
       ├─→ Calculate test statistics
       │   └─→ Total, Passed, Failed, Success Rate
       │
       ├─→ Print TEST EXECUTION SUMMARY
       │   └─→ "Total Tests: 10"
       │   └─→ "Passed: 10 ✓"
       │   └─→ "Failed: 0 ✗"
       │   └─→ "Success Rate: 100.0%"
       │
       ▼
pytest-html plugin generates report
       │
       └─→ reports/report.html
```

---

## 🔄 Complete Flow Diagram (Start to Finish)

```
┌─────────────────────────────────────────────────────────────┐
│ Step 1: User runs → py -m pytest tests/ -v                  │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 2: Pytest reads pytest.ini + conftest.py               │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 3: Session fixtures → Playwright, Browser (ONE TIME)   │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 4: Test 1 starts → Function fixtures (context, page)   │
│         New browser context created (isolated)               │
│         New page created                                     │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 5: test_verify_website_title() executes                │
│         page.goto(base_url) → Auto-wait for load            │
│         LoginPage → Interact (auto-wait built-in!)          │
│         assert → Verify result                              │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 6: pytest_runtest_makereport → Capture result          │
│         page.screenshot() → Full page capture               │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 7: page.close() + context.close()                      │
│         Browser stays open ✓                                 │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 8: Repeat Steps 4-7 for remaining tests (fast!)        │
│         Each test: New context → Test → Close context       │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 9: browser.close() → Close browser after all tests     │
│         Print TEST EXECUTION SUMMARY with counts             │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 10: DONE! 10 passed in 32.08s                          │
│          Report: reports/report.html                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 🗂️ File Interaction Map

```
User Command
    │
    ├─→ run_tests.bat  OR  py -m pytest tests/
    │       │
    │       └─→ pytest.ini
    │               │
    │               └─→ conftest.py (Playwright fixtures & hooks)
    │                       │
    │                       └─→ tests/test_saucedemo_playwright.py
    │                               │
    │                               ├─→ pages/base_page.py
    │                               ├─→ pages/login_page.py
    │                               ├─→ pages/inventory_page.py
    │                               └─→ pages/cart_page.py
    │
    └─→ Output
            ├─→ reports/report.html
            └─→ reports/screenshots/success/*.png
            └─→ reports/screenshots/failure/*.png
```

---

## 📚 Key Playwright Concepts

### **1. Browser Context**
```python
# One browser, multiple isolated contexts
browser = playwright.chromium.launch()

context1 = browser.new_context()  # User 1 session
context2 = browser.new_context()  # User 2 session

# Completely isolated:
# - Separate cookies
# - Separate localStorage
# - Separate cache
```

### **2. Auto-Waiting**
```python
# Playwright automatically waits for:
page.click("#button")
    ├─→ Element exists
    ├─→ Element visible
    ├─→ Element enabled
    ├─→ Element stable
    └─→ Then clicks

# No need for explicit waits!
```

### **3. Powerful Selectors**
```python
# CSS selectors
page.click("#submit-button")
page.click(".btn-primary")

# Text selectors
page.click("text=Login")
page.click("button:has-text('Submit')")

# XPath
page.click("//button[@id='submit']")

# Chaining
page.locator("#form").locator("button").click()
```

### **4. Network Interception**
```python
# Mock API responses (advanced feature!)
page.route("**/api/products", lambda route: route.fulfill(
    status=200,
    body='{"products": []}'
))
```

---

## 🎯 Playwright vs Selenium Comparison

### **Code Comparison:**

**Selenium:**
```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

driver = webdriver.Chrome()
driver.get("https://example.com")

# Manual waits needed
wait = WebDriverWait(driver, 10)
button = wait.until(EC.element_to_be_clickable((By.ID, "submit")))
button.click()

driver.quit()
```

**Playwright:**
```python
from playwright.sync_api import sync_playwright

with sync_playwright() as p:
    browser = p.chromium.launch()
    page = browser.new_page()
    page.goto("https://example.com")
    
    # Auto-wait built-in!
    page.click("#submit")
    
    browser.close()
```

---

## ⚙️ Configuration

### **pytest.ini**
```ini
[pytest]
testpaths = tests
python_files = test_*.py
addopts = -v --html=reports/report.html
filterwarnings =
    ignore::RuntimeWarning:.*greenlet.*
markers =
    smoke: Quick smoke tests
    regression: Full regression tests
```

### **Environment Variables**
```bash
# Browser selection
set BROWSER=chromium  # or firefox, webkit
set HEADLESS=false
```

---

## 🚀 Playwright Advantages

| Feature | Benefit |
|---------|---------|
| **Auto-wait** | No explicit waits needed |
| **Fast** | Reuses browser between tests |
| **Multi-browser** | Chromium, Firefox, WebKit (Safari) |
| **Network control** | Mock APIs, intercept requests |
| **Mobile** | Built-in mobile emulation |
| **Screenshots** | Full-page, high-quality |
| **Video** | Built-in video recording |
| **Parallel** | Multiple contexts in one browser |

---

## 🎯 Test Execution Summary Feature

```
After all tests complete, console shows:

======================================================================
TEST EXECUTION SUMMARY
======================================================================
Total Tests: 10
Passed: 10 ✓
Failed: 0 ✗
Success Rate: 100.0%
Exit Status: 0
======================================================================
```

**How it works:**
```python
# conftest.py
@pytest.hookimpl(tryfirst=True, hookwrapper=True)
def pytest_runtest_makereport(item, call):
    # Track pass/fail
    if report.passed:
        item.report_passed = True
    elif report.failed:
        item.report_failed = True

def pytest_sessionfinish(session, exitstatus):
    # Calculate totals
    passed = sum(1 for item in session.items if item.report_passed)
    failed = sum(1 for item in session.items if item.report_failed)
    total = len(session.items)
    
    # Print summary
    print(f"Total Tests: {total}")
    print(f"Passed: {passed} ✓")
    print(f"Failed: {failed} ✗")
```

---

## 🔧 Troubleshooting

### **Browser not maximized?**
Fixed in conftest.py:
```python
context = browser.new_context(
    no_viewport=True  # ← Allows full maximization
)
```

### **GIL Warning?**
Suppressed in pytest.ini:
```ini
filterwarnings =
    ignore::RuntimeWarning:.*greenlet.*
```

**Explanation:** Python 3.13 warning about greenlet library (used internally by Playwright). Harmless, will be fixed in future greenlet release.

---

**Last Updated:** 2025-11-28  
**Framework Version:** 1.0  
**Playwright Version:** 1.48.0
