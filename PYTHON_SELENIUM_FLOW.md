# Python Selenium Framework - Flow of Execution

Complete flow diagram showing how Python Selenium framework executes with Pytest.

---

## 🎯 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                PYTHON SELENIUM FRAMEWORK                         │
│                  (Web Automation - Pytest)                       │
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
        │Driver Fixture│ │Page Objects  │ │Screenshot    │
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
Found: test_saucedemo_comprehensive.py
       │
       └─→ Class: TestSauceDemoComprehensive
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
       └─→ markers = smoke, regression
       │
       ▼
conftest.py loaded (BEFORE tests run)
       │
       └─→ All fixtures defined here become available to tests
```

---

### **Step 3: Session Setup (pytest_configure)**

```
conftest.py → pytest_configure() hook
       │
       ├─→ Create reports directory
       │   └─→ Path("reports").mkdir(exist_ok=True)
       │
       └─→ Print framework banner
           └─→ "PYTHON SELENIUM AUTOMATION FRAMEWORK"
           └─→ Browser: chrome (from environment or default)
           └─→ Headless: false
```

---

### **Step 4: Fixture Creation (Session Scope)**

```
Session-scoped fixtures run ONCE for entire test session:
       │
       ├─→ @pytest.fixture(scope="session")
       │   def base_url():
       │       return "https://www.saucedemo.com"
       │
       └─→ @pytest.fixture(scope="session")
           def test_credentials():
               return {
                   "valid_username": "standard_user",
                   "valid_password": "secret_sauce",
                   ...
               }
```

---

### **Step 5: Test Method Fixture Injection (Function Scope)**

```
Before EACH test method:
       │
       ▼
@pytest.fixture(scope="function")
def driver(request):
    """Create browser for each test"""
       │
       ▼
┌──────────────────────────────────────────────────────────┐
│           Create Chrome WebDriver                         │
└──────────────────────────────────────────────────────────┘
       │
       ├─→ Read BROWSER environment variable (default: chrome)
       ├─→ Read HEADLESS environment variable (default: false)
       │
       ▼
┌──────────────────────────────────────────────────────────┐
│         WebDriverManager (Auto Driver Download)           │
└──────────────────────────────────────────────────────────┐
       │
       ├─→ from webdriver_manager.chrome import ChromeDriverManager
       ├─→ Check ChromeDriver version
       ├─→ Download if needed
       └─→ Return driver binary path
       │
       ▼
┌──────────────────────────────────────────────────────────┐
│              Create Chrome Options                        │
└──────────────────────────────────────────────────────────┘
       │
       ├─→ --start-maximized
       ├─→ --disable-blink-features=AutomationControlled
       ├─→ --no-sandbox
       ├─→ --disable-dev-shm-usage
       ├─→ Disable password manager popups
       └─→ --headless=new (if headless mode)
       │
       ▼
driver = webdriver.Chrome(service=service, options=chrome_options)
       │
       ├─→ Set implicit wait: 5 seconds
       │
       ▼
yield driver  ← Give driver to test
       │
       (Test runs here)
       │
       ▼
finally:
    driver.quit()  ← Close browser after test
```

**Key Difference from Java:**
- Pytest **automatically injects** fixtures into test methods
- No need to call setup/teardown explicitly
- Just declare fixture as parameter → Pytest provides it!

---

### **Step 6: Test Execution**

```
test_saucedemo_comprehensive.py
       │
       ▼
class TestSauceDemoComprehensive:
       │
       ▼
def test_verify_website_title(self, driver, base_url):
    """Pytest automatically injects driver and base_url fixtures"""
       │
       ├─→ Pytest sees 'driver' parameter
       │   └─→ Calls driver() fixture
       │       └─→ Creates Chrome browser
       │       └─→ Returns driver to test
       │
       ├─→ Pytest sees 'base_url' parameter
       │   └─→ Calls base_url() fixture
       │       └─→ Returns "https://www.saucedemo.com"
       │
       ▼
Test method executes:
       │
       ├─→ driver.get(base_url)
       │   └─→ Browser navigates to SauceDemo
       │
       ├─→ login_page = LoginPage(driver)
       │   └─→ LoginPage constructor receives driver
       │       └─→ Stores: self.driver = driver
       │
       ├─→ assert "Swag Labs" in driver.title
       │   ├─→ If True: Test PASSES ✓
       │   └─→ If False: Test FAILS ✗
       │
       └─→ print("✓ Website title verified successfully")
```

---

### **Step 7: Page Object Interaction**

```
┌──────────────────────────────────────────────────────────────┐
│                    login_page.py                              │
└──────────────────────────────────────────────────────────────┘
       │
       ▼
class LoginPage(BasePage):
    """Login page object"""
    
    # Locators (tuples of By strategy and value)
    USERNAME_INPUT = (By.ID, "user-name")
    PASSWORD_INPUT = (By.ID, "password")
    LOGIN_BUTTON = (By.ID, "login-button")
       │
       ▼
def login(self, username, password):
    """Perform login"""
       │
       ├─→ username_field = self.driver.find_element(*self.USERNAME_INPUT)
       │   └─→ * unpacks tuple: find_element(By.ID, "user-name")
       │
       ├─→ username_field.clear()
       ├─→ username_field.send_keys("standard_user")
       │
       ├─→ password_field = self.driver.find_element(*self.PASSWORD_INPUT)
       ├─→ password_field.clear()
       ├─→ password_field.send_keys("secret_sauce")
       │
       └─→ login_button = self.driver.find_element(*self.LOGIN_BUTTON)
           └─→ login_button.click()
```

**Python vs Java:**
```python
# Python: Uses tuples for locators
USERNAME_INPUT = (By.ID, "user-name")
element = self.driver.find_element(*self.USERNAME_INPUT)

# Java: Uses @FindBy annotations
@FindBy(id = "user-name")
private WebElement usernameField;
```

---

### **Step 8: Test Result Capture (Hook)**

```
After EACH test completes:
       │
       ▼
conftest.py → pytest_runtest_makereport() hook
       │
       ├─→ Captures test result (passed/failed)
       │
       ▼
if report.when == "call":
       │
       ├─→ Get page fixture from test
       │   └─→ page = item.funcargs["driver"]
       │
       ▼
Take Screenshot
       │
       ├─→ If PASSED:
       │   └─→ Save to: reports/screenshots/success/
       │       └─→ test_verify_website_title_SUCCESS_20251128_123456.png
       │
       └─→ If FAILED:
           └─→ Save to: reports/screenshots/failure/
               └─→ test_login_with_invalid_credentials_FAILURE_20251128_123457.png
```

---

### **Step 9: Fixture Cleanup**

```
After test completes:
       │
       ▼
driver fixture cleanup (finally block)
       │
       ├─→ driver.quit()
       │   └─→ Close browser window
       │   └─→ Kill ChromeDriver process
       │
       └─→ print("WebDriver quit successfully")
```

---

### **Step 10: Next Test Execution**

```
Pytest moves to next test method:
       │
       ├─→ driver fixture runs again
       │   └─→ Creates NEW browser instance
       │       └─→ Fresh session (no cookies, no cache)
       │
       ├─→ Test executes
       │
       └─→ driver fixture cleanup
           └─→ Closes browser
```

**Important:** Each test gets a **fresh browser** (function scope).

---

### **Step 11: Session Finish**

```
After ALL tests complete:
       │
       ▼
conftest.py → pytest_sessionfinish() hook
       │
       ├─→ Print "TEST SESSION FINISHED"
       ├─→ Print exit status (0 = success, 1 = failures)
       │
       ▼
pytest-html plugin generates report
       │
       ├─→ Compiles all test results
       ├─→ Embeds screenshots
       ├─→ Creates: reports/report.html
       │
       └─→ Self-contained HTML (CSS embedded)
```

---

## 🔄 Complete Flow Diagram (Start to Finish)

```
┌─────────────────────────────────────────────────────────────┐
│ Step 1: User runs → py -m pytest tests/ -v                  │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 2: Pytest reads pytest.ini configuration               │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 3: pytest_configure hook → Setup reports directory     │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 4: Session fixtures created (base_url, test_credentials)│
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 5: Test 1 starts → Pytest injects fixtures             │
│         driver fixture → Creates Chrome browser              │
│         base_url fixture → Returns URL                       │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 6: test_verify_website_title() executes                │
│         driver.get(base_url) → Navigate to site             │
│         LoginPage → Interact with page                      │
│         assert → Verify result                              │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 7: pytest_runtest_makereport → Capture result          │
│         Take screenshot → Save to success/ or failure/       │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 8: driver fixture cleanup → driver.quit()              │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 9: Repeat Steps 5-8 for remaining tests                │
│         test_login_with_valid_credentials()                 │
│         test_login_with_invalid_credentials()               │
│         ... (more tests)                                    │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 10: pytest_sessionfinish → Print summary               │
│          pytest-html → Generate HTML report                  │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 11: DONE! 6 passed in 35.2s                            │
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
    │               └─→ conftest.py (fixtures & hooks)
    │                       │
    │                       └─→ tests/test_saucedemo_comprehensive.py
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

## 📚 Key Files and Their Roles

### **1. conftest.py**
- **Role:** Pytest configuration and fixtures
- **Contains:**
  - Session fixtures (base_url, test_credentials)
  - Function fixtures (driver)
  - Hooks (pytest_configure, pytest_runtest_makereport, pytest_sessionfinish)
- **Special:** Automatically loaded by Pytest

### **2. pytest.ini**
- **Role:** Pytest configuration file
- **Contains:**
  - Test discovery patterns
  - Command-line options (addopts)
  - Custom markers (smoke, regression)

### **3. base_page.py**
- **Role:** Common page methods
- **Methods:**
  - `click(selector)` → Click element
  - `type_text(selector, text)` → Type in input
  - `wait_for_element(selector)` → Wait for element
  - `is_visible(selector)` → Check visibility

### **4. login_page.py**
- **Role:** Login page interactions
- **Inherits:** BasePage
- **Locators:**
  - `USERNAME_INPUT = (By.ID, "user-name")`
  - `PASSWORD_INPUT = (By.ID, "password")`
  - `LOGIN_BUTTON = (By.ID, "login-button")`
- **Methods:**
  - `login(username, password)` → Perform login
  - `is_error_displayed()` → Check for error message

### **5. test_saucedemo_comprehensive.py**
- **Role:** Test methods
- **Structure:**
  ```python
  class TestSauceDemoComprehensive:
      def test_verify_website_title(self, driver, base_url):
          """Test receives fixtures automatically"""
          # Test code here
  ```

---

## 🎯 Pytest Fixture System

### **How Fixtures Work:**

```
Test Method Signature:
def test_login(self, driver, base_url, test_credentials):
                      │        │             │
                      └────────┼─────────────┘
                               │
                    Pytest automatically finds & calls
                    these fixtures from conftest.py
```

### **Fixture Scopes:**

```
scope="session"  → Runs ONCE for entire test session
    └─→ Example: base_url, test_credentials
    └─→ Created once, shared by all tests

scope="function" → Runs BEFORE and AFTER each test
    └─→ Example: driver
    └─→ Fresh browser for each test
```

### **Fixture Dependency:**

```
@pytest.fixture(scope="function")
def driver(browser_type_name, headless_mode):
    """driver fixture depends on other fixtures"""
    # browser_type_name and headless_mode are also fixtures!
```

---

## 🎯 Data Flow

```
Environment Variables
    │
    ├─→ BROWSER=chrome
    └─→ HEADLESS=false
         │
         ▼
conftest.py (reads via os.getenv)
         │
         ▼
Fixture returns values
         │
         ▼
Test methods receive values
         │
         ▼
Page Objects use driver
         │
         ▼
Browser automation
```

---

## 🎨 Python vs Java TestNG Comparison

| Feature | Python Pytest | Java TestNG |
|---------|---------------|-------------|
| **Setup** | `@pytest.fixture` | `@BeforeMethod` |
| **Teardown** | `yield` + cleanup | `@AfterMethod` |
| **Data** | Fixture injection | XML parameters |
| **Assertions** | `assert` statement | `Assert.assertEquals()` |
| **Test Discovery** | Auto (test_*.py) | XML configuration |
| **Reports** | pytest-html plugin | ExtentReports |
| **Parallel** | pytest-xdist | TestNG parallel |

---

## ⚙️ Configuration

### **pytest.ini**
```ini
[pytest]
testpaths = tests
python_files = test_*.py
addopts = -v --html=reports/report.html
markers =
    smoke: Quick smoke tests
    regression: Full regression tests
```

### **conftest.py (key fixtures)**
```python
@pytest.fixture(scope="session")
def base_url():
    return "https://www.saucedemo.com"

@pytest.fixture(scope="function")
def driver():
    # Create browser
    yield driver_instance
    # Cleanup: driver.quit()
```

---

## 🎯 Pytest Markers Usage

```bash
# Run only smoke tests
py -m pytest tests/ -m smoke

# Run only regression tests
py -m pytest tests/ -m regression

# Run tests matching keyword
py -m pytest tests/ -k "login"
```

**In code:**
```python
@pytest.mark.smoke
def test_login_with_valid_credentials(self, driver, base_url):
    """This test will run when -m smoke is used"""
    pass
```

---

**Last Updated:** 2025-11-28  
**Framework Version:** 1.0
