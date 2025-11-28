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
    
    # Launch args for maximized window
    launch_args = []
    if browser_type_name == 'chromium':
        launch_args = ['--start-maximized', '--disable-blink-features=AutomationControlled']
    
    browser = browser_type.launch(
        headless=headless_mode,
        args=launch_args
    )
    yield browser
    browser.close()


@pytest.fixture(scope="function")
def context(browser):
    """Create a new browser context for each test"""
    # Use no_viewport for maximized window behavior
    context = browser.new_context(
        no_viewport=True,
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
    
    # Track pass/fail for summary
    if report.when == "call":
        if report.passed:
            item.report_passed = True
            item.report_failed = False
        elif report.failed:
            item.report_passed = False
            item.report_failed = True
    
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
    
    # Suppress GIL warning for greenlet
    import warnings
    warnings.filterwarnings('ignore', message='.*GIL.*greenlet.*', category=RuntimeWarning)
    
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
    # Calculate pass/fail counts
    passed = sum(1 for item in session.items if hasattr(item, 'report_passed') and item.report_passed)
    failed = sum(1 for item in session.items if hasattr(item, 'report_failed') and item.report_failed)
    total = len(session.items)
    
    print("\n" + "="*70)
    print("TEST EXECUTION SUMMARY")
    print("="*70)
    print(f"Total Tests: {total}")
    print(f"Passed: {passed} ✓")
    print(f"Failed: {failed} ✗")
    print(f"Success Rate: {(passed/total*100) if total > 0 else 0:.1f}%")
    print(f"Exit Status: {exitstatus}")
    print("="*70)
