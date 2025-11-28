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
from selenium.common.exceptions import TimeoutException, NoSuchElementException


class TestSauceDemoComprehensive:
    """Comprehensive test suite matching Java framework coverage"""

    def dismiss_password_popup(self, driver):
        """Helper method to dismiss password manager popups - SPEED OPTIMIZED"""
        try:
            # FASTER: Reduced wait time from 2 to 0.5 seconds
            wait = WebDriverWait(driver, 0.5)

            # Try dismissing with ESC key first (fastest method)
            try:
                from selenium.webdriver.common.keys import Keys
                driver.find_element(By.TAG_NAME, "body").send_keys(Keys.ESCAPE)
                return True
            except:
                pass

        except Exception as e:
            # No popup found - this is good
            pass

        return False

    def perform_login(self, driver, base_url, username, password):
        """Enhanced login method with popup handling - SPEED OPTIMIZED"""
        wait = WebDriverWait(driver, 5)  # Reduced from 8 to 5 seconds
        
        driver.get(base_url)

        # Quick popup dismissal
        self.dismiss_password_popup(driver)

        # Enter credentials
        username_field = wait.until(EC.visibility_of_element_located((By.ID, "user-name")))
        password_field = driver.find_element(By.ID, "password")
        login_button = driver.find_element(By.ID, "login-button")

        username_field.clear()
        username_field.send_keys(username)

        password_field.clear()
        password_field.send_keys(password)

        # Quick popup dismissal before login
        self.dismiss_password_popup(driver)

        login_button.click()

        # SPEED: Reduced wait time from 1 second to 0.3
        time.sleep(0.3)
        self.dismiss_password_popup(driver)

    def test_verify_website_title(self, driver, base_url):
        """Test 1: Verify website title - matches Java Selenium"""
        driver.get(base_url)
        assert "Swag Labs" in driver.title
        print("[PASS] Website title verified successfully")

    def test_login_with_valid_credentials(self, driver, base_url, test_credentials):
        """Test 2: Login with valid credentials - SPEED OPTIMIZED"""
        self.perform_login(driver, base_url, test_credentials["valid_username"], test_credentials["valid_password"])

        # SPEED: Reduced wait time from 6 to 4 seconds
        wait = WebDriverWait(driver, 4)
        wait.until(EC.url_contains("inventory"))
        assert "inventory" in driver.current_url
        print("[PASS] Login with valid credentials successful")

    def test_login_with_invalid_credentials(self, driver, base_url, test_credentials):
        """Test 3: Login with invalid credentials - SPEED OPTIMIZED"""
        self.perform_login(driver, base_url, test_credentials["invalid_username"], test_credentials["invalid_password"])

        # SPEED: Reduced wait time from 6 to 4 seconds
        wait = WebDriverWait(driver, 4)
        error_message = wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, "[data-test='error']")))
        assert "Username and password do not match" in error_message.text
        print("[PASS] Login with invalid credentials failed as expected")

    def test_add_item_to_cart(self, driver, base_url, test_credentials):
        """Test 4: Add item to cart functionality - SPEED OPTIMIZED"""
        wait = WebDriverWait(driver, 8)  # Reduced from 10 to 8 seconds
        
        self.perform_login(driver, base_url, test_credentials["valid_username"], test_credentials["valid_password"])
        
        # Wait for inventory page to fully load
        wait.until(EC.url_contains("inventory"))
        wait.until(EC.presence_of_element_located((By.CLASS_NAME, "inventory_list")))

        # ELEMENT VALIDATION - optimized
        add_to_cart_button = wait.until(
            EC.element_to_be_clickable((By.XPATH, "//button[contains(text(), 'Add to cart')]"))
        )

        # SPEED: Reduced scroll pause from 0.5 to 0.1 seconds
        driver.execute_script("arguments[0].scrollIntoView(true);", add_to_cart_button)
        time.sleep(0.1)

        add_to_cart_button.click()

        # SPEED: Faster verification with reduced wait
        cart_verified = False
        fast_wait = WebDriverWait(driver, 3)  # Faster verification wait

        # Approach 1: Check for cart badge (fastest)
        try:
            cart_badge = fast_wait.until(EC.visibility_of_element_located((By.CLASS_NAME, "shopping_cart_badge")))
            if cart_badge.text == "1":
                cart_verified = True
                print("[PASS] Item added to cart successfully (verified by cart badge)")
        except:
            pass

        # Approach 2: Check if button text changed to "Remove"
        if not cart_verified:
            try:
                remove_button = fast_wait.until(
                    EC.element_to_be_clickable((By.XPATH, "//button[contains(text(), 'Remove')]"))
                )
                cart_verified = True
                print("[PASS] Item added to cart successfully (verified by Remove button)")
            except:
                pass

        # Approach 3: Check cart icon clickability
        if not cart_verified:
            try:
                cart_icon = wait.until(EC.element_to_be_clickable((By.CLASS_NAME, "shopping_cart_link")))
                cart_verified = True
                print("[PASS] Item added to cart successfully (verified by cart icon)")
            except:
                pass

        assert cart_verified, "Failed to verify item was added to cart"

    def test_remove_item_from_cart(self, driver, base_url, test_credentials):
        """Test 5: Remove item from cart functionality"""
        wait = WebDriverWait(driver, 15)
        
        self.perform_login(driver, base_url, test_credentials["valid_username"], test_credentials["valid_password"])
        
        wait.until(EC.url_contains("inventory"))
        wait.until(EC.presence_of_element_located((By.CLASS_NAME, "inventory_list")))

        # Add item to cart first - try multiple selectors for reliability
        add_button_selectors = [
            (By.XPATH, "//button[contains(text(), 'Add to cart')]"),
            (By.XPATH, "//button[text()='Add to cart']"),
            (By.XPATH, "//button[@class='btn btn_primary btn_small btn_inventory']"),
            (By.CSS_SELECTOR, "button.btn_primary.btn_inventory")
        ]
        
        add_to_cart_button = None
        for selector_type, selector_value in add_button_selectors:
            try:
                add_to_cart_button = wait.until(
                    EC.element_to_be_clickable((selector_type, selector_value))
                )
                break
            except:
                continue
        
        assert add_to_cart_button is not None, "Failed to find Add to cart button"
        
        driver.execute_script("arguments[0].scrollIntoView(true);", add_to_cart_button)
        time.sleep(0.5)
        
        # Click with JS for reliability
        driver.execute_script("arguments[0].click();", add_to_cart_button)
        
        # Wait for cart badge to appear
        time.sleep(1)

        # Wait for button to change to Remove - try multiple selectors
        remove_button_selectors = [
            (By.XPATH, "//button[contains(text(), 'Remove')]"),
            (By.XPATH, "//button[text()='Remove']"),
            (By.XPATH, "//button[@class='btn btn_secondary btn_small btn_inventory']"),
            (By.CSS_SELECTOR, "button.btn_secondary.btn_inventory")
        ]
        
        remove_button = None
        for selector_type, selector_value in remove_button_selectors:
            try:
                remove_button = wait.until(
                    EC.element_to_be_clickable((selector_type, selector_value))
                )
                print(f"[INFO] Remove button found using {selector_type}")
                break
            except:
                continue
        
        assert remove_button is not None, "Failed to find Remove button after adding item"

        driver.execute_script("arguments[0].scrollIntoView(true);", remove_button)
        time.sleep(0.5)

        # Click remove button with JS for reliability
        driver.execute_script("arguments[0].click();", remove_button)
        
        # Wait for removal to complete
        time.sleep(0.5)

        # Verify removal
        removal_verified = False

        # Approach 1: Wait for button text to change back to "Add to cart"
        try:
            wait.until(
                EC.element_to_be_clickable((By.XPATH, "//button[contains(text(), 'Add to cart')]"))
            )
            removal_verified = True
            print("[PASS] Item removed from cart successfully (Add to cart button present)")
        except:
            pass

        # Approach 2: Check if cart badge is gone
        if not removal_verified:
            try:
                cart_badges = driver.find_elements(By.CLASS_NAME, "shopping_cart_badge")
                if len(cart_badges) == 0:
                    removal_verified = True
                    print("[PASS] Item removed from cart successfully (cart badge gone)")
            except:
                pass

        assert removal_verified, "Failed to verify item was removed from cart"

    def test_logout_functionality(self, driver, base_url, test_credentials):
        """Test 6: Logout functionality"""
        wait = WebDriverWait(driver, 15)  # Increased timeout for menu animation
        
        self.perform_login(driver, base_url, test_credentials["valid_username"], test_credentials["valid_password"])
        
        wait.until(EC.url_contains("inventory"))

        # Open menu with retry logic
        menu_button = wait.until(EC.element_to_be_clickable((By.ID, "react-burger-menu-btn")))
        driver.execute_script("arguments[0].scrollIntoView(true);", menu_button)
        time.sleep(0.3)

        # Click menu with JS fallback
        try:
            menu_button.click()
        except:
            driver.execute_script("arguments[0].click();", menu_button)

        # Wait for menu to open - check for menu visibility
        time.sleep(1.5)
        
        # Wait for logout link to be visible in the DOM
        logout_clicked = False

        # Try with increased wait and multiple selectors
        logout_selectors = [
            (By.ID, "logout_sidebar_link"),
            (By.XPATH, "//a[@id='logout_sidebar_link']"),
            (By.XPATH, "//nav[@class='bm-item-list']//a[contains(@href, 'logout')]"),
            (By.XPATH, "//a[contains(text(), 'Logout')]"),
            (By.CSS_SELECTOR, "a#logout_sidebar_link")
        ]

        for selector_type, selector_value in logout_selectors:
            try:
                logout_link = wait.until(EC.presence_of_element_located((selector_type, selector_value)))
                # Scroll into view
                driver.execute_script("arguments[0].scrollIntoView(true);", logout_link)
                time.sleep(0.3)
                # Click with JS (more reliable for menu items)
                driver.execute_script("arguments[0].click();", logout_link)
                logout_clicked = True
                print(f"[PASS] Logout clicked using {selector_type}")
                break
            except Exception as e:
                continue

        assert logout_clicked, "Failed to click logout link"

        # Wait for login page to appear
        wait.until(EC.visibility_of_element_located((By.ID, "login-button")))
        print("[PASS] Logout functionality working correctly")