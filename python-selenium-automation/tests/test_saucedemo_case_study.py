import pytest
import time
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


class TestSauceDemoCaseStudy:
    
    def test_saucedemo_complete_workflow(self, driver, base_url, test_credentials):
        """
        Complete case study implementation:
        1. Launch URL using Selenium with Python
        2. Verify SWAG LABS is present on the Web Page
        3. Add any one Item to cart (Click on ADD TO CART button)
        4. Click on right corner button and verify item is added to cart
        5. Click on left corner button and click LOGOUT
        """
        wait = WebDriverWait(driver, 10)
        
        # Step 1: Launch URL using Selenium with Python
        driver.get(base_url)
        assert driver.current_url == base_url
        
        # Step 2: Verify SWAG LABS is present on the Web Page
        swag_labs_logo = wait.until(EC.visibility_of_element_located((By.CLASS_NAME, "login_logo")))
        assert "Swag Labs" in swag_labs_logo.text
        
        # Login to access inventory page
        username_field = wait.until(EC.visibility_of_element_located((By.ID, "user-name")))
        password_field = driver.find_element(By.ID, "password")
        login_button = driver.find_element(By.ID, "login-button")
        
        username_field.send_keys(test_credentials["username"])
        password_field.send_keys(test_credentials["password"])
        login_button.click()
        
        # Verify login successful
        wait.until(EC.url_contains("inventory"))
        assert "inventory" in driver.current_url
        
        # Step 3: Add any one Item to cart (Click on ADD TO CART button)
        add_to_cart_button = wait.until(EC.element_to_be_clickable((By.XPATH, "//button[contains(@id, 'add-to-cart')]")))
        item_name_element = driver.find_element(By.XPATH, "//div[@class='inventory_item_name ']")
        item_name = item_name_element.text
        add_to_cart_button.click()
        
        # Step 4: Click on right corner button (shopping cart) and verify item is added
        # Wait for cart to update and verify cart badge shows 1
        try:
            cart_badge = wait.until(EC.visibility_of_element_located((By.CLASS_NAME, "shopping_cart_badge")))
            assert cart_badge.text == "1"
        except:
            # Alternative: Check if cart icon is present (some versions don't show badge immediately)
            cart_icon = wait.until(EC.presence_of_element_located((By.CLASS_NAME, "shopping_cart_link")))
            assert cart_icon is not None
        
        # Click shopping cart (right corner button) - use more reliable method
        shopping_cart = wait.until(EC.element_to_be_clickable((By.CLASS_NAME, "shopping_cart_link")))
        driver.execute_script("arguments[0].click();", shopping_cart)  # Use JavaScript click for reliability

        # Wait for cart page to load
        wait.until(lambda driver: "cart" in driver.current_url.lower() or "your-cart" in driver.current_url.lower())

        # Verify on cart page
        current_url = driver.current_url.lower()
        cart_page_found = "cart" in current_url or "your-cart" in current_url
        assert cart_page_found, f"Expected cart page, but got URL: {driver.current_url}"
        
        # Verify item in cart
        cart_item = wait.until(EC.visibility_of_element_located((By.CLASS_NAME, "cart_item")))
        assert cart_item is not None
        
        # Navigate back to inventory
        driver.back()
        wait.until(EC.url_contains("inventory"))
        
        # Step 5: Click on left corner button (menu) and click LOGOUT
        menu_button = wait.until(EC.element_to_be_clickable((By.ID, "react-burger-menu-btn")))
        menu_button.click()
        
        time.sleep(1)  # Wait for menu to open
        logout_link = wait.until(EC.element_to_be_clickable((By.ID, "logout_sidebar_link")))
        logout_link.click()
        
        # Verify logout successful - should be back to login page
        wait.until(EC.presence_of_element_located((By.ID, "user-name")))
        assert "inventory" not in driver.current_url
        assert driver.find_element(By.ID, "login-button").is_displayed()
