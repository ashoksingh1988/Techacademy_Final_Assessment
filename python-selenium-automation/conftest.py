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
def driver():
    """WebDriver fixture for tests - OPTIMIZED for fast execution"""
    global driver_instance

    # Chrome options with SPEED-OPTIMIZED configuration
    chrome_options = Options()
    chrome_options.add_argument("--start-maximized")
    chrome_options.add_argument("--disable-blink-features=AutomationControlled")
    chrome_options.add_experimental_option("excludeSwitches", ["enable-automation"])
    chrome_options.add_experimental_option('useAutomationExtension', False)

    # SPEED OPTIMIZATION: Disable resource-intensive features
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-dev-shm-usage")
    chrome_options.add_argument("--disable-gpu")
    chrome_options.add_argument("--disable-software-rasterizer")
    chrome_options.add_argument("--disable-background-timer-throttling")
    chrome_options.add_argument("--disable-backgrounding-occluded-windows")
    chrome_options.add_argument("--disable-renderer-backgrounding")
    chrome_options.add_argument("--disable-field-trial-config")
    chrome_options.add_argument("--disable-ipc-flooding-protection")

    # ENHANCED FIX: COMPLETELY DISABLE PASSWORD MANAGER & POPUPS
    chrome_options.add_experimental_option("prefs", {
        # Password Manager Settings
        "credentials_enable_service": False,
        "profile.password_manager_enabled": False,
        "profile.default_content_setting_values.notifications": 2,
        "autofill.profile_enabled": False,
        "autofill.credit_card_enabled": False,

        # Additional Password & Security Settings
        "password_manager_enabled": False,
        "credentials_enable_autosignin": False,
        "profile.password_manager_leak_detection": False,
        "profile.default_content_settings.popups": 0,
        "profile.managed_default_content_settings.popups": 0,

        # SPEED: Disable resource-heavy features
        "profile.default_content_setting_values.media_stream_mic": 2,
        "profile.default_content_setting_values.media_stream_camera": 2,
        "profile.default_content_setting_values.geolocation": 2,
        "profile.default_content_setting_values.desktop_notification": 2,

        # Privacy and Security - optimized
        "safebrowsing.enabled": False,
        "safebrowsing.disable_download_protection": True,
        "profile.default_content_setting_values.automatic_downloads": 1,
    })

    # AGGRESSIVE POPUP & NOTIFICATION BLOCKING
    chrome_options.add_argument("--disable-extensions")
    chrome_options.add_argument("--disable-plugins")
    chrome_options.add_argument("--disable-images")  # Faster loading
    chrome_options.add_argument("--disable-javascript-harmony-shipping")
    chrome_options.add_argument("--disable-background-timer-throttling")
    chrome_options.add_argument("--disable-backgrounding-occluded-windows")
    chrome_options.add_argument("--disable-renderer-backgrounding")
    chrome_options.add_argument("--disable-features=TranslateUI")
    chrome_options.add_argument("--disable-infobars")

    # NEW: ADDITIONAL POPUP BLOCKING ARGUMENTS
    chrome_options.add_argument("--disable-popup-blocking")
    chrome_options.add_argument("--disable-notifications")
    chrome_options.add_argument("--disable-save-password-bubble")
    chrome_options.add_argument("--disable-password-generation")
    chrome_options.add_argument("--disable-password-manager-reauthentication")
    chrome_options.add_argument("--disable-sync")
    chrome_options.add_argument("--disable-web-security")
    chrome_options.add_argument("--disable-features=VizDisplayCompositor")
    chrome_options.add_argument("--disable-features=PasswordImport")
    chrome_options.add_argument("--disable-features=PasswordExport")

    # NEW: MAXIMUM SPEED OPTIMIZATIONS
    chrome_options.add_argument("--aggressive-cache-discard")
    chrome_options.add_argument("--memory-pressure-off")
    chrome_options.add_argument("--max_old_space_size=4096")
    chrome_options.add_argument("--disable-background-networking")
    chrome_options.add_argument("--disable-default-apps")
    chrome_options.add_argument("--disable-component-extensions-with-background-pages")

    # Check for headless mode from environment
    headless = os.getenv('HEADLESS', 'false').lower() == 'true'
    if headless:
        chrome_options.add_argument("--headless")
        chrome_options.add_argument("--virtual-time-budget=5000")  # Speed up headless mode
    
    # Chrome binary path from environment
    chrome_binary = os.getenv('CHROME_BINARY')
    if chrome_binary and os.path.exists(chrome_binary):
        chrome_options.binary_location = chrome_binary
    
    try:
        # Use system ChromeDriver or specify path
        chromedriver_path = r"C:\Program Files\Google\Chrome\Application\chromedriver.exe"
        if not os.path.exists(chromedriver_path):
            # Fallback to webdriver-manager
            from webdriver_manager.chrome import ChromeDriverManager
            chromedriver_path = ChromeDriverManager().install()
            # Fix the path if it's pointing to the wrong file
            if 'THIRD_PARTY_NOTICES' in chromedriver_path:
                base_dir = os.path.dirname(chromedriver_path)
                chromedriver_path = os.path.join(base_dir, 'chromedriver.exe')
        
        service = Service(chromedriver_path)
        driver_instance = webdriver.Chrome(service=service, options=chrome_options)

        # Enhanced configuration: Reduced implicit wait for better explicit wait handling
        driver_instance.implicitly_wait(5)  # Reduced from 10 to 5 seconds

        # NEW: EXECUTE JAVASCRIPT TO DISABLE PASSWORD MANAGER AT RUNTIME
        driver_instance.execute_script("""
            // Disable password manager at runtime
            Object.defineProperty(navigator, 'credentials', {
                get: () => undefined
            });
            
            // Block password manager UI
            window.addEventListener('beforeunload', function(e) {
                e.returnValue = '';
            });
            
            // Override password manager methods
            if (window.PasswordCredential) {
                window.PasswordCredential = undefined;
            }
        """)

        print(f"WebDriver initialized successfully with ENHANCED popup blocking")
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
