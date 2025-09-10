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
    """WebDriver fixture for tests"""
    global driver_instance
    
    # Chrome options
    chrome_options = Options()
    chrome_options.add_argument("--start-maximized")
    chrome_options.add_argument("--disable-blink-features=AutomationControlled")
    chrome_options.add_experimental_option("excludeSwitches", ["enable-automation"])
    chrome_options.add_experimental_option('useAutomationExtension', False)
    
    # Check for headless mode from environment
    headless = os.getenv('HEADLESS', 'false').lower() == 'true'
    if headless:
        chrome_options.add_argument("--headless")
    
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
        driver_instance.implicitly_wait(10)
        
        print(f"WebDriver initialized successfully")
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
