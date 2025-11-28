from playwright.sync_api import Page
from pages.base_page import BasePage


class InventoryPage(BasePage):
    """Inventory/Products page object"""
    
    # Locators
    INVENTORY_CONTAINER = ".inventory_container"
    INVENTORY_LIST = ".inventory_list"
    INVENTORY_ITEM = ".inventory_item"
    PRODUCT_NAME = ".inventory_item_name"
    ADD_TO_CART_BUTTON = ".btn_inventory"
    REMOVE_BUTTON = "[data-test^='remove-']"
    SHOPPING_CART_BADGE = ".shopping_cart_badge"
    SHOPPING_CART_LINK = ".shopping_cart_link"
    MENU_BUTTON = "#react-burger-menu-btn"
    LOGOUT_LINK = "#logout_sidebar_link"
    PRODUCT_SORT_DROPDOWN = ".product_sort_container"
    
    def __init__(self, page: Page):
        super().__init__(page)
    
    def is_inventory_page_displayed(self) -> bool:
        """Check if inventory page is displayed"""
        return self.is_visible(self.INVENTORY_CONTAINER)
    
    def get_product_count(self) -> int:
        """Get number of products displayed"""
        return self.get_element_count(self.INVENTORY_ITEM)
    
    def add_first_item_to_cart(self):
        """Add first item to cart"""
        self.page.locator(self.ADD_TO_CART_BUTTON).first.click()
        self.wait(0.5)
    
    def remove_first_item_from_cart(self):
        """Remove first item from cart"""
        self.page.locator(self.REMOVE_BUTTON).first.click()
        self.wait(0.5)
    
    def get_cart_badge_count(self) -> str:
        """Get shopping cart badge count"""
        if self.is_visible(self.SHOPPING_CART_BADGE):
            return self.get_text(self.SHOPPING_CART_BADGE)
        return "0"
    
    def click_shopping_cart(self):
        """Click shopping cart icon"""
        self.click(self.SHOPPING_CART_LINK)
        self.wait(0.5)
    
    def open_menu(self):
        """Open burger menu"""
        self.click(self.MENU_BUTTON)
        self.wait(0.5)
    
    def logout(self):
        """Logout from application"""
        self.open_menu()
        self.click(self.LOGOUT_LINK)
        self.wait(0.5)
    
    def is_remove_button_visible(self) -> bool:
        """Check if remove button is visible"""
        return self.is_visible(self.REMOVE_BUTTON)
    
    def sort_products(self, option: str):
        """Sort products by option (az, za, lohi, hilo)"""
        self.page.select_option(self.PRODUCT_SORT_DROPDOWN, option)
        self.wait(0.5)
