import pytest
import os
from datetime import datetime
from playwright.sync_api import sync_playwright, Page, Browser, BrowserContext
from pathlib import Path


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
        "locked_username": "locked_out_user",
        "invalid_username": "invalid_user",
        "invalid_password": "invalid_password"
    }


@pytest.fixture(scope="session")
def browser_type_name():
    """Browser type from environment variable or default to chromium"""
    return os.getenv('BROWSER', 'chromium').lower()


@pytest.fixture(scope="session")
def headless_mode():
    """Headless mode from environment variable or default to False"""
    return os.getenv('HEADLESS', 'false').lower() == 'true'


@pytest.fixture(scope="session")
def playwright_instance():
    """Create Playwright instance for the session"""
    with sync_playwright() as playwright:
        yield playwright


@pytest.fixture(scope="session")
def browser(playwright_instance, browser_type_name, headless_mode):
    """Launch browser for the session"""
    browser_types = {
        'chromium': playwright_instance.chromium,
        'firefox': playwright_instance.firefox,
        'webkit': playwright_instance.webkit
    }
    
    browser_type = browser_types.get(browser_type_name, playwright_instance.chromium)
    browser = browser_type.launch(
        headless=headless_mode,
        args=['--start-maximized'] if browser_type_name == 'chromium' else []
    )
    yield browser
    browser.close()


@pytest.fixture(scope="function")
def context(browser):
    """Create a new browser context for each test"""
    context = browser.new_context(
        viewport={'width': 1920, 'height': 1080},
        ignore_https_errors=True
    )
    yield context
    context.close()


@pytest.fixture(scope="function")
def page(context):
    """Create a new page for each test"""
    page = context.new_page()
    yield page
    page.close()


@pytest.hookimpl(tryfirst=True, hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """Hook to capture test results and take screenshots"""
    outcome = yield
    report = outcome.get_result()
    
    if report.when == "call":
        # Get the page fixture if it exists
        if "page" in item.funcargs:
            page = item.funcargs["page"]
            
            # Create screenshots directory
            screenshot_dir = Path("reports/screenshots")
            if report.passed:
                screenshot_dir = screenshot_dir / "success"
            else:
                screenshot_dir = screenshot_dir / "failure"
            
            screenshot_dir.mkdir(parents=True, exist_ok=True)
            
            # Generate screenshot filename
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            status = "SUCCESS" if report.passed else "FAILURE"
            screenshot_name = f"{item.name}_{status}_{timestamp}.png"
            screenshot_path = screenshot_dir / screenshot_name
            
            # Take screenshot
            try:
                page.screenshot(path=str(screenshot_path), full_page=True)
                print(f"\nScreenshot saved: {screenshot_path}")
            except Exception as e:
                print(f"\nFailed to capture screenshot: {str(e)}")


def pytest_configure(config):
    """Pytest configuration hook"""
    # Create reports directory
    reports_dir = Path("reports")
    reports_dir.mkdir(exist_ok=True)
    
    print("\n" + "="*70)
    print("PLAYWRIGHT AUTOMATION FRAMEWORK")
    print("="*70)
    print(f"Browser: {os.getenv('BROWSER', 'chromium')}")
    print(f"Headless: {os.getenv('HEADLESS', 'false')}")
    print("="*70 + "\n")


def pytest_sessionstart(session):
    """Called before test session starts"""
    print("TEST SESSION STARTED")
    print(f"Total tests collected: {len(session.items) if hasattr(session, 'items') else 'Collecting...'}")


def pytest_sessionfinish(session, exitstatus):
    """Called after test session finishes"""
    print("\n" + "="*70)
    print("TEST SESSION FINISHED")
    print(f"Exit Status: {exitstatus}")
    print("="*70)
