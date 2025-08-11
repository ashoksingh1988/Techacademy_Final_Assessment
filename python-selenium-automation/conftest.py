import pytest
import logging
import os
from selenium import webdriver
from selenium.webdriver.chrome.options import Options

# Suppress Selenium and urllib3 warnings
logging.getLogger('selenium').setLevel(logging.WARNING)
logging.getLogger('urllib3').setLevel(logging.WARNING)
os.environ['WDM_LOG_LEVEL'] = '0'


@pytest.fixture(scope="function")
def driver():
    """
    Pytest fixture to initialize and quit WebDriver
    """
    chrome_options = Options()
    chrome_options.add_argument("--disable-notifications")
    chrome_options.add_argument("--disable-popup-blocking")
    chrome_options.add_argument("--start-maximized")
    chrome_options.add_argument("--disable-dev-shm-usage")
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-logging")
    chrome_options.add_argument("--log-level=3")
    chrome_options.add_argument("--silent")
    chrome_options.add_argument("--disable-web-security")
    chrome_options.add_argument("--disable-features=VizDisplayCompositor")
    chrome_options.add_experimental_option("useAutomationExtension", False)
    chrome_options.add_experimental_option("excludeSwitches", ["enable-automation"])

    # Suppress console warnings
    import os
    os.environ['WDM_LOG_LEVEL'] = '0'

    driver = webdriver.Chrome(options=chrome_options)
    driver.implicitly_wait(10)
    driver.set_page_load_timeout(30)

    yield driver

    driver.quit()


@pytest.fixture(scope="session")
def base_url():
    return "https://www.saucedemo.com/"


@pytest.fixture(scope="session")
def test_credentials():
    return {
        "username": "standard_user",
        "password": "secret_sauce"
    }
