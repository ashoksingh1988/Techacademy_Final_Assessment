import pytest
from pages.login_page import LoginPage
from pages.inventory_page import InventoryPage
from pages.cart_page import CartPage


class TestSauceDemoPlaywright:
    """Comprehensive test suite for SauceDemo using Playwright"""
    
    @pytest.mark.smoke
    def test_verify_website_title(self, page, base_url):
        """Verify website title is correct"""
        page.goto(base_url)
        login_page = LoginPage(page)
        
        assert login_page.is_login_page_displayed(), "Login page should be displayed"
        assert "Swag Labs" in page.title(), "Page title should contain 'Swag Labs'"
        print("✓ Website title verified successfully")
    
    @pytest.mark.smoke
    @pytest.mark.login
    def test_login_with_valid_credentials(self, page, base_url, test_credentials):
        """Test login with valid credentials"""
        page.goto(base_url)
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)
        
        # Perform login
        login_page.login(
            test_credentials["valid_username"],
            test_credentials["valid_password"]
        )
        
        # Verify successful login
        assert inventory_page.is_inventory_page_displayed(), "Should be on inventory page"
        assert "inventory.html" in page.url, "URL should contain 'inventory.html'"
        print("✓ Login with valid credentials successful")
    
    @pytest.mark.smoke
    @pytest.mark.login
    def test_login_with_invalid_credentials(self, page, base_url, test_credentials):
        """Test login with invalid credentials shows error"""
        page.goto(base_url)
        login_page = LoginPage(page)
        
        # Attempt login with invalid credentials
        login_page.login(
            test_credentials["invalid_username"],
            test_credentials["invalid_password"]
        )
        
        # Verify error message is displayed
        assert login_page.is_error_displayed(), "Error message should be displayed"
        error_text = login_page.get_error_message()
        assert "Epic sadface" in error_text, "Error message should contain 'Epic sadface'"
        print(f"✓ Error message displayed correctly: {error_text}")
    
    @pytest.mark.smoke
    @pytest.mark.login
    def test_login_with_locked_user(self, page, base_url, test_credentials):
        """Test login with locked user shows appropriate error"""
        page.goto(base_url)
        login_page = LoginPage(page)
        
        # Attempt login with locked user
        login_page.login(
            test_credentials["locked_username"],
            test_credentials["valid_password"]
        )
        
        # Verify error message
        assert login_page.is_error_displayed(), "Error message should be displayed"
        error_text = login_page.get_error_message()
        assert "locked out" in error_text.lower(), "Error should mention user is locked"
        print(f"✓ Locked user error displayed: {error_text}")
    
    @pytest.mark.regression
    @pytest.mark.cart
    def test_add_item_to_cart(self, page, base_url, test_credentials):
        """Test adding item to shopping cart"""
        page.goto(base_url)
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)
        
        # Login
        login_page.login(
            test_credentials["valid_username"],
            test_credentials["valid_password"]
        )
        
        # Add item to cart
        inventory_page.add_first_item_to_cart()
        
        # Verify cart badge shows 1 item
        cart_count = inventory_page.get_cart_badge_count()
        assert cart_count == "1", f"Cart should show 1 item, but shows {cart_count}"
        assert inventory_page.is_remove_button_visible(), "Remove button should be visible"
        print("✓ Item added to cart successfully")
    
    @pytest.mark.regression
    @pytest.mark.cart
    def test_remove_item_from_cart(self, page, base_url, test_credentials):
        """Test removing item from shopping cart"""
        page.goto(base_url)
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)
        
        # Login and add item
        login_page.login(
            test_credentials["valid_username"],
            test_credentials["valid_password"]
        )
        inventory_page.add_first_item_to_cart()
        
        # Remove item
        inventory_page.remove_first_item_from_cart()
        
        # Verify cart is empty
        cart_count = inventory_page.get_cart_badge_count()
        assert cart_count == "0", f"Cart should be empty, but shows {cart_count}"
        print("✓ Item removed from cart successfully")
    
    @pytest.mark.regression
    @pytest.mark.cart
    def test_view_cart_page(self, page, base_url, test_credentials):
        """Test viewing cart page"""
        page.goto(base_url)
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)
        cart_page = CartPage(page)
        
        # Login, add item, and go to cart
        login_page.login(
            test_credentials["valid_username"],
            test_credentials["valid_password"]
        )
        inventory_page.add_first_item_to_cart()
        inventory_page.click_shopping_cart()
        
        # Verify cart page
        assert cart_page.is_cart_page_displayed(), "Cart page should be displayed"
        assert cart_page.get_cart_item_count() == 1, "Cart should contain 1 item"
        assert "cart.html" in page.url, "URL should contain 'cart.html'"
        print("✓ Cart page displayed successfully")
    
    @pytest.mark.regression
    @pytest.mark.cart
    def test_remove_item_from_cart_page(self, page, base_url, test_credentials):
        """Test removing item from cart page"""
        page.goto(base_url)
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)
        cart_page = CartPage(page)
        
        # Login, add item, and go to cart
        login_page.login(
            test_credentials["valid_username"],
            test_credentials["valid_password"]
        )
        inventory_page.add_first_item_to_cart()
        inventory_page.click_shopping_cart()
        
        # Remove item from cart page
        cart_page.remove_item_from_cart()
        
        # Verify cart is empty
        assert cart_page.is_cart_empty(), "Cart should be empty"
        print("✓ Item removed from cart page successfully")
    
    @pytest.mark.regression
    def test_logout_functionality(self, page, base_url, test_credentials):
        """Test logout functionality"""
        page.goto(base_url)
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)
        
        # Login
        login_page.login(
            test_credentials["valid_username"],
            test_credentials["valid_password"]
        )
        
        # Logout
        inventory_page.logout()
        
        # Verify back on login page
        assert login_page.is_login_page_displayed(), "Should be back on login page"
        assert base_url in page.url, "URL should be base URL"
        print("✓ Logout successful")
    
    @pytest.mark.regression
    def test_product_count(self, page, base_url, test_credentials):
        """Test product count on inventory page"""
        page.goto(base_url)
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)
        
        # Login
        login_page.login(
            test_credentials["valid_username"],
            test_credentials["valid_password"]
        )
        
        # Verify product count
        product_count = inventory_page.get_product_count()
        assert product_count == 6, f"Expected 6 products, found {product_count}"
        print(f"✓ Product count verified: {product_count} products")
