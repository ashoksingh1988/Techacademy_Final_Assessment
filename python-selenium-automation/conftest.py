"""
Enhanced pytest configuration with comprehensive reporting integration
Features: Success/Failure Screenshots, Performance Tracking, Enhanced Reporting

@author: Asim Kumar Singh
@version: 2.0.0 - Enhanced for Lead Review Requirements
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
from webdriver_manager.chrome import ChromeDriverManager

# Add utils to path
sys.path.append(os.path.join(os.path.dirname(__file__), 'utils'))

from enhanced_report_manager import enhanced_report_manager
from enhanced_screenshot_utils import enhanced_screenshot_utils

# Global driver instance
driver_instance = None

@pytest.fixture(scope="session", autouse=True)
def setup_enhanced_reporting():
    """Setup enhanced reporting for the entire test session"""
    print("\n🚀 Initializing Enhanced Python Selenium Reporting...")
    
    # Clean up old screenshots at session start
    enhanced_screenshot_utils.cleanup_old_screenshots(7)
    
    yield
    
    # Generate reports at session end
    print("\n📊 Generating Enhanced Reports...")
    html_report_path = enhanced_report_manager.generate_html_report()
    json_report_path = enhanced_report_manager.save_json_report()
    
    # Get final statistics
    screenshot_stats = enhanced_screenshot_utils.get_screenshot_statistics()
    
    print(f"✅ Enhanced HTML Report: {html_report_path}")
    print(f"✅ JSON Report: {json_report_path}")
    print(f"📸 Screenshots Captured: {screenshot_stats['total_count']}")
    print(f"   • Success: {screenshot_stats['success_count']}")
    print(f"   • Failure: {screenshot_stats['failure_count']}")
    print(f"   • Steps: {screenshot_stats['step_count']}")

@pytest.fixture(scope="function")
def setup_driver():
    """Setup WebDriver with enhanced configuration"""
    global driver_instance
    
    print("\n🌐 Setting up Enhanced WebDriver...")
    
    # Chrome options
    chrome_options = Options()
    
    # Get configuration from environment variables
    headless_mode = os.getenv('HEADLESS_MODE', 'true').lower() == 'true'
    browser_binary = os.getenv('CHROME_BINARY', None)
    
    if headless_mode:
        chrome_options.add_argument('--headless')
        print("   • Running in headless mode")
    else:
        print("   • Running in GUI mode")
    
    if browser_binary and os.path.exists(browser_binary):
        chrome_options.binary_location = browser_binary
        print(f"   • Using custom Chrome binary: {browser_binary}")
    
    # Additional Chrome options for stability
    chrome_options.add_argument('--no-sandbox')
    chrome_options.add_argument('--disable-dev-shm-usage')
    chrome_options.add_argument('--disable-gpu')
    chrome_options.add_argument('--window-size=1920,1080')
    chrome_options.add_argument('--disable-extensions')
    chrome_options.add_argument('--disable-plugins')
    chrome_options.add_argument('--disable-images')  # Faster loading
    chrome_options.add_argument('--disable-javascript')  # For basic testing
    
    # Performance optimization
    chrome_options.add_experimental_option('useAutomationExtension', False)
    chrome_options.add_experimental_option("excludeSwitches", ["enable-automation"])
    chrome_options.add_argument('--disable-blink-features=AutomationControlled')
    
    try:
        # Setup ChromeDriver
        service = Service(ChromeDriverManager().install())
        driver_instance = webdriver.Chrome(service=service, options=chrome_options)
        
        # Configure timeouts
        driver_instance.implicitly_wait(10)
        driver_instance.set_page_load_timeout(30)
        
        print("✅ WebDriver setup completed successfully")
        
        yield driver_instance
        
    except Exception as e:
        print(f"❌ WebDriver setup failed: {str(e)}")
        raise
    finally:
        # Cleanup
        if driver_instance:
            try:
                driver_instance.quit()
                print("🔄 WebDriver cleanup completed")
            except Exception as e:
                print(f"⚠️ WebDriver cleanup warning: {str(e)}")
        driver_instance = None

@pytest.hookimpl(tryfirst=True, hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """Enhanced test reporting hook with comprehensive logging"""
    outcome = yield
    report = outcome.get_result()
    
    # Get test information
    test_name = item.name
    test_description = item.function.__doc__ if item.function.__doc__ else f"Test: {test_name}"
    
    if call.when == "setup":
        # Test setup phase
        if report.outcome == "failed":
            print(f"❌ Setup failed for {test_name}: {report.longrepr}")
    
    elif call.when == "call":
        # Main test execution phase
        if report.outcome == "passed":
            # Test passed
            print(f"✅ Test passed: {test_name}")
            
            # Capture success screenshot if driver is available
            if driver_instance:
                try:
                    screenshot_path = enhanced_screenshot_utils.capture_success_screenshot(
                        driver_instance, test_name, "Test completed successfully"
                    )
                    enhanced_report_manager.log_pass(
                        f"✅ TEST PASSED: {test_name}", screenshot_path
                    )
                except Exception as e:
                    print(f"⚠️ Failed to capture success screenshot: {str(e)}")
                    enhanced_report_manager.log_pass(f"✅ TEST PASSED: {test_name}")
            else:
                enhanced_report_manager.log_pass(f"✅ TEST PASSED: {test_name}")
        
        elif report.outcome == "failed":
            # Test failed
            error_message = str(report.longrepr) if report.longrepr else "Unknown error"
            print(f"❌ Test failed: {test_name} - {error_message}")
            
            # Capture failure screenshot if driver is available
            if driver_instance:
                try:
                    screenshot_path = enhanced_screenshot_utils.capture_failure_screenshot(
                        driver_instance, test_name, f"Test failed: {error_message}"
                    )
                    enhanced_report_manager.log_fail(
                        f"❌ TEST FAILED: {test_name}", screenshot_path, error_message
                    )
                except Exception as e:
                    print(f"⚠️ Failed to capture failure screenshot: {str(e)}")
                    enhanced_report_manager.log_fail(
                        f"❌ TEST FAILED: {test_name}", None, error_message
                    )
            else:
                enhanced_report_manager.log_fail(
                    f"❌ TEST FAILED: {test_name}", None, error_message
                )
        
        elif report.outcome == "skipped":
            # Test skipped
            skip_reason = str(report.longrepr) if report.longrepr else "Unknown reason"
            print(f"⏭️ Test skipped: {test_name} - {skip_reason}")
            enhanced_report_manager.log_skip(f"⏭️ TEST SKIPPED: {test_name}", skip_reason)
    
    elif call.when == "teardown":
        # Test teardown phase
        if report.outcome == "failed":
            print(f"❌ Teardown failed for {test_name}: {report.longrepr}")

@pytest.hookimpl(tryfirst=True)
def pytest_runtest_setup(item):
    """Enhanced test setup hook"""
    test_name = item.name
    test_description = item.function.__doc__ if item.function.__doc__ else f"Test: {test_name}"
    
    print(f"\n🚀 Starting test: {test_name}")
    
    # Start test in enhanced report manager
    enhanced_report_manager.start_test(test_name, test_description)
    
    # Log test start details
    enhanced_report_manager.log_step("Test execution started")
    enhanced_report_manager.log_info(f"Test Module: {item.module.__name__}")
    enhanced_report_manager.log_info(f"Test Function: {test_name}")
    enhanced_report_manager.log_info(f"Test Description: {test_description}")

def pytest_configure(config):
    """Configure pytest with enhanced settings"""
    # Add custom markers
    config.addinivalue_line(
        "markers", "smoke: mark test as smoke test"
    )
    config.addinivalue_line(
        "markers", "regression: mark test as regression test"
    )
    config.addinivalue_line(
        "markers", "performance: mark test as performance test"
    )

def pytest_collection_modifyitems(config, items):
    """Modify test collection for enhanced reporting"""
    print(f"\n📋 Collected {len(items)} tests for enhanced execution")
    
    # Add markers based on test names or patterns
    for item in items:
        # Auto-add smoke marker for tests containing 'smoke' in name
        if 'smoke' in item.name.lower():
            item.add_marker(pytest.mark.smoke)
        
        # Auto-add regression marker for tests containing 'regression' in name
        if 'regression' in item.name.lower():
            item.add_marker(pytest.mark.regression)

# Helper functions for test methods
def log_test_step(step_description: str):
    """Log a test step with enhanced reporting"""
    print(f"📝 Step: {step_description}")
    enhanced_report_manager.log_step(step_description)

def log_test_step_with_screenshot(step_description: str, test_name: str):
    """Log a test step with screenshot"""
    print(f"📝 Step: {step_description}")
    
    if driver_instance:
        try:
            screenshot_path = enhanced_screenshot_utils.capture_step_screenshot(
                driver_instance, test_name, step_description
            )
            enhanced_report_manager.log_step_completion(step_description)
            if screenshot_path:
                enhanced_report_manager.log_info(f"Step screenshot captured: {step_description}")
        except Exception as e:
            print(f"⚠️ Failed to capture step screenshot: {str(e)}")
            enhanced_report_manager.log_step_completion(step_description)
    else:
        enhanced_report_manager.log_step_completion(step_description)

def log_performance_metric(action: str, response_time_ms: float, url: str = ""):
    """Log performance metrics during test execution"""
    print(f"📊 Performance: {action} completed in {response_time_ms:.0f}ms")
    
    performance_level = "🚀 Excellent" if response_time_ms < 1000 else \
                       "✅ Good" if response_time_ms < 3000 else \
                       "⚠️ Acceptable" if response_time_ms < 5000 else \
                       "❌ Poor"
    
    enhanced_report_manager.log_web_performance_metrics(action, response_time_ms, url)
    enhanced_report_manager.log_info(f"📊 Performance: {action} | {response_time_ms:.0f}ms | {performance_level}")

def capture_custom_screenshot(description: str):
    """Capture a custom screenshot during test execution"""
    if driver_instance:
        try:
            screenshot_path = enhanced_screenshot_utils.capture_screenshot_with_custom_name(
                driver_instance, description
            )
            if screenshot_path:
                enhanced_report_manager.log_info(f"Custom screenshot captured: {description}")
                return screenshot_path
        except Exception as e:
            print(f"⚠️ Failed to capture custom screenshot: {str(e)}")
    return None

# Export helper functions for use in test files
__all__ = [
    'setup_driver',
    'log_test_step',
    'log_test_step_with_screenshot', 
    'log_performance_metric',
    'capture_custom_screenshot'
]
