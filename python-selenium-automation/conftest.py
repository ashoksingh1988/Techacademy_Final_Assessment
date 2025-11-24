"""
pytest configuration for Python Selenium Automation Framework

@author: Asim Kumar Singh
@version: 1.0.0
"""

import pytest
import os
import sys
import time
import traceback
from datetime import datetime
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.firefox.service import Service as FirefoxService
from selenium.webdriver.firefox.options import Options as FirefoxOptions
from selenium.webdriver.edge.service import Service as EdgeService
from selenium.webdriver.edge.options import Options as EdgeOptions

# Global driver instance
driver_instance = None

@pytest.fixture(scope="session", autouse=True)
def setup_reporting():
    """Setup test reporting"""
    print("Setting up Python Selenium test reporting...")
    yield
    print("Test reporting setup completed")

@pytest.fixture(scope="session")
def base_url():
    """Base URL for the application"""
    return "https://www.saucedemo.com"

@pytest.fixture(scope="session")
def test_credentials():
    """Test credentials for login"""
    return {
        "valid_username": "standard_user",
        "valid_password": "secret_sauce",
        "invalid_username": "invalid_user",
        "invalid_password": "invalid_password"
    }

@pytest.fixture(scope="function")
def driver(request):
    """WebDriver fixture for tests - OPTIMIZED for fast execution"""
    global driver_instance

    # Get browser type from environment or default to chrome
    browser = os.getenv('BROWSER', 'chrome').lower()
    headless = os.getenv('HEADLESS', 'false').lower() == 'true'
    
    try:
        if browser == 'firefox':
            driver_instance = create_firefox_driver(headless)
        elif browser == 'edge':
            driver_instance = create_edge_driver(headless)
        else:
            driver_instance = create_chrome_driver(headless)
        
        # Enhanced configuration: Reduced implicit wait for better explicit wait handling
        driver_instance.implicitly_wait(5)  # Reduced from 10 to 5 seconds

        print(f"WebDriver initialized successfully for {browser}")
        yield driver_instance
        
    except Exception as e:
        print(f"Error initializing WebDriver: {str(e)}")
        raise
    finally:
        if driver_instance:
            try:
                driver_instance.quit()
                print("WebDriver quit successfully")
            except Exception as e:
                print(f"Error quitting WebDriver: {str(e)}")

def create_chrome_driver(headless):
    """Create Chrome WebDriver with optimized options"""
    chrome_options = Options()
    
    # Basic options
    chrome_options.add_argument("--start-maximized")
    chrome_options.add_argument("--disable-blink-features=AutomationControlled")
    chrome_options.add_experimental_option("excludeSwitches", ["enable-automation", "enable-logging"])
    chrome_options.add_experimental_option('useAutomationExtension', False)

    # Essential stability options
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-dev-shm-usage")
    chrome_options.add_argument("--disable-gpu")
    chrome_options.add_argument("--remote-debugging-port=0")  # Disable remote debugging

    # Password Manager & Popup Settings
    chrome_options.add_experimental_option("prefs", {
        "credentials_enable_service": False,
        "profile.password_manager_enabled": False,
        "profile.default_content_setting_values.notifications": 2,
        "autofill.profile_enabled": False,
    })

    # Notification and popup blocking
    chrome_options.add_argument("--disable-notifications")
    chrome_options.add_argument("--disable-infobars")

    if headless:
        chrome_options.add_argument("--headless=new")  # Use new headless mode
        chrome_options.add_argument("--window-size=1920,1080")
    
    # Chrome binary path from environment
    chrome_binary = os.getenv('CHROME_BINARY')
    if chrome_binary and os.path.exists(chrome_binary):
        chrome_options.binary_location = chrome_binary
    
    # Use webdriver-manager to automatically download matching ChromeDriver
    from webdriver_manager.chrome import ChromeDriverManager
    
    try:
        # Install ChromeDriver matching the installed Chrome version
        service = Service(ChromeDriverManager().install())
        return webdriver.Chrome(service=service, options=chrome_options)
    except Exception as e:
        print(f"Error creating Chrome driver with webdriver-manager: {str(e)}")
        # Try alternative approach
        service = Service()
        return webdriver.Chrome(service=service, options=chrome_options)

def create_firefox_driver(headless):
    """Create Firefox WebDriver with optimized options"""
    firefox_options = FirefoxOptions()
    
    if headless:
        firefox_options.add_argument("--headless")
    
    firefox_options.add_argument("--width=1920")
    firefox_options.add_argument("--height=1080")
    
    # Firefox preferences for faster execution
    firefox_options.set_preference("dom.max_script_run_time", 30)
    firefox_options.set_preference("dom.max_chrome_script_run_time", 30)
    firefox_options.set_preference("browser.download.folderList", 2)
    firefox_options.set_preference("browser.download.manager.showWhenStarting", False)
    firefox_options.set_preference("browser.helperApps.neverAsk.saveToDisk", "text/plain, application/octet-stream, application/pdf, text/csv, application/csv, text/html, application/xhtml+xml, application/xml, application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    
    # Use webdriver-manager for Firefox
    from webdriver_manager.firefox import GeckoDriverManager
    service = FirefoxService(GeckoDriverManager().install())
    return webdriver.Firefox(service=service, options=firefox_options)

def create_edge_driver(headless):
    """Create Edge WebDriver with optimized options"""
    edge_options = EdgeOptions()
    
    if headless:
        edge_options.add_argument("--headless")
    
    edge_options.add_argument("--no-sandbox")
    edge_options.add_argument("--disable-dev-shm-usage")
    edge_options.add_argument("--disable-gpu")
    edge_options.add_argument("--window-size=1920,1080")
    
    # Edge-specific optimizations
    edge_options.add_argument("--disable-background-timer-throttling")
    edge_options.add_argument("--disable-backgrounding-occluded-windows")
    edge_options.add_argument("--disable-renderer-backgrounding")
    
    # Use webdriver-manager for Edge
    from webdriver_manager.microsoft import EdgeChromiumDriverManager
    service = EdgeService(EdgeChromiumDriverManager().install())
    return webdriver.Edge(service=service, options=edge_options)

@pytest.hookimpl(tryfirst=True, hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """Hook to capture test results and take screenshots"""
    outcome = yield
    report = outcome.get_result()
    
    if report.when == "call":
        test_name = item.name
        timestamp = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
        
        # Create reports directory if it doesn't exist
        reports_dir = os.path.join(os.path.dirname(__file__), "reports", "screenshots")
        os.makedirs(reports_dir, exist_ok=True)
        
        if report.passed:
            # Success screenshot
            success_dir = os.path.join(reports_dir, "success")
            os.makedirs(success_dir, exist_ok=True)
            
            if driver_instance:
                try:
                    screenshot_path = os.path.join(success_dir, f"{test_name}_SUCCESS_{timestamp}.png")
                    driver_instance.save_screenshot(screenshot_path)
                    print(f"Success screenshot saved: {screenshot_path}")
                except Exception as e:
                    print(f"Failed to capture success screenshot: {str(e)}")
        
        elif report.failed:
            # Failure screenshot
            failure_dir = os.path.join(reports_dir, "failure")
            os.makedirs(failure_dir, exist_ok=True)
            
            if driver_instance:
                try:
                    screenshot_path = os.path.join(failure_dir, f"{test_name}_FAILURE_{timestamp}.png")
                    driver_instance.save_screenshot(screenshot_path)
                    print(f"Failure screenshot saved: {screenshot_path}")
                except Exception as e:
                    print(f"Failed to capture failure screenshot: {str(e)}")

def pytest_configure(config):
    """Configure pytest with custom settings"""
    # Ensure reports directory exists
    reports_dir = os.path.join(os.path.dirname(__file__), "reports")
    os.makedirs(reports_dir, exist_ok=True)
    
    print("Python Selenium Automation Framework configured successfully")

def pytest_sessionstart(session):
    """Called after the Session object has been created"""
    print("=" * 80)
    print("PYTHON SELENIUM AUTOMATION FRAMEWORK - TEST SESSION STARTED")
    print("=" * 80)

def pytest_sessionfinish(session, exitstatus):
    """Called after whole test run finished"""
    print("=" * 80)
    print("PYTHON SELENIUM AUTOMATION FRAMEWORK - TEST SESSION FINISHED")
    print(f"Exit Status: {exitstatus}")
    print("=" * 80)