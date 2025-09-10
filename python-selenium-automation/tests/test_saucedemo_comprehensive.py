"""
Comprehensive SauceDemo Test Suite for Python Selenium Framework
Matches Java framework test coverage for consistent execution

@author: Asim Kumar Singh
@version: 1.0.0
"""

import pytest
import time
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


class TestSauceDemoComprehensive:
    """Comprehensive test suite matching Java framework coverage"""

    def test_verify_website_title(self, driver, base_url):
        """Test 1: Verify website title - matches Java Selenium"""
        driver.get(base_url)
        assert "Swag Labs" in driver.title
        print("✅ Website title verified successfully")

    def test_login_with_valid_credentials(self, driver, base_url, test_credentials):
        """Test 2: Login with valid credentials - matches Java Selenium"""
        wait = WebDriverWait(driver, 10)
        
        driver.get(base_url)
        
        # Enter valid credentials
        username_field = wait.until(EC.visibility_of_element_located((By.ID, "user-name")))
        password_field = driver.find_element(By.ID, "password")
        login_button = driver.find_element(By.ID, "login-button")
        
        username_field.send_keys(test_credentials["valid_username"])
        password_field.send_keys(test_credentials["valid_password"])
        login_button.click()
        
        # Verify successful login
        wait.until(EC.url_contains("inventory"))
        assert "inventory" in driver.current_url
        print("✅ Login with valid credentials successful")

    def test_login_with_invalid_credentials(self, driver, base_url, test_credentials):
        """Test 3: Login with invalid credentials - matches Java Selenium"""
        wait = WebDriverWait(driver, 10)
        
        driver.get(base_url)
        
        # Enter invalid credentials
        username_field = wait.until(EC.visibility_of_element_located((By.ID, "user-name")))
        password_field = driver.find_element(By.ID, "password")
        login_button = driver.find_element(By.ID, "login-button")
        
        username_field.send_keys(test_credentials["invalid_username"])
        password_field.send_keys(test_credentials["invalid_password"])
        login_button.click()
        
        # Verify error message appears
        error_message = wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, "[data-test='error']")))
        assert "Username and password do not match" in error_message.text
        print("✅ Login with invalid credentials failed as expected")

    def test_add_item_to_cart(self, driver, base_url, test_credentials):
        """Test 4: Add item to cart functionality"""
        wait = WebDriverWait(driver, 10)
        
        # Login first
        driver.get(base_url)
        username_field = wait.until(EC.visibility_of_element_located((By.ID, "user-name")))
        password_field = driver.find_element(By.ID, "password")
        login_button = driver.find_element(By.ID, "login-button")
        
        username_field.send_keys(test_credentials["valid_username"])
        password_field.send_keys(test_credentials["valid_password"])
        login_button.click()
        
        # Wait for inventory page
        wait.until(EC.url_contains("inventory"))
        
        # Add first item to cart
        add_to_cart_button = wait.until(EC.element_to_be_clickable((By.XPATH, "//button[contains(@id, 'add-to-cart')]")))
        add_to_cart_button.click()
        
        # Verify cart badge shows 1
        cart_badge = wait.until(EC.visibility_of_element_located((By.CLASS_NAME, "shopping_cart_badge")))
        assert cart_badge.text == "1"
        print("✅ Item added to cart successfully")

    def test_remove_item_from_cart(self, driver, base_url, test_credentials):
        """Test 5: Remove item from cart functionality"""
        wait = WebDriverWait(driver, 10)
        
        # Login and add item first
        driver.get(base_url)
        username_field = wait.until(EC.visibility_of_element_located((By.ID, "user-name")))
        password_field = driver.find_element(By.ID, "password")
        login_button = driver.find_element(By.ID, "login-button")
        
        username_field.send_keys(test_credentials["valid_username"])
        password_field.send_keys(test_credentials["valid_password"])
        login_button.click()
        
        wait.until(EC.url_contains("inventory"))
        
        # Add item to cart
        add_to_cart_button = wait.until(EC.element_to_be_clickable((By.XPATH, "//button[contains(@id, 'add-to-cart')]")))
        add_to_cart_button.click()
        
        # Remove item from cart
        remove_button = wait.until(EC.element_to_be_clickable((By.XPATH, "//button[contains(@id, 'remove')]")))
        remove_button.click()
        
        # Verify cart badge is gone
        cart_badges = driver.find_elements(By.CLASS_NAME, "shopping_cart_badge")
        assert len(cart_badges) == 0
        print("✅ Item removed from cart successfully")

    def test_logout_functionality(self, driver, base_url, test_credentials):
        """Test 6: Logout functionality"""
        wait = WebDriverWait(driver, 10)
        
        # Login first
        driver.get(base_url)
        username_field = wait.until(EC.visibility_of_element_located((By.ID, "user-name")))
        password_field = driver.find_element(By.ID, "password")
        login_button = driver.find_element(By.ID, "login-button")
        
        username_field.send_keys(test_credentials["valid_username"])
        password_field.send_keys(test_credentials["valid_password"])
        login_button.click()
        
        wait.until(EC.url_contains("inventory"))
        
        # Open menu and logout
        menu_button = wait.until(EC.element_to_be_clickable((By.ID, "react-burger-menu-btn")))
        menu_button.click()
        
        logout_link = wait.until(EC.element_to_be_clickable((By.ID, "logout_sidebar_link")))
        logout_link.click()
        
        # Verify back to login page
        wait.until(EC.visibility_of_element_located((By.ID, "login-button")))
        assert base_url in driver.current_url
        print("✅ Logout functionality working correctly")
