from playwright.sync_api import Page
from pages.base_page import BasePage


class CartPage(BasePage):
    """Shopping cart page object"""
    
    # Locators
    CART_CONTAINER = ".cart_contents_container"
    CART_ITEM = ".cart_item"
    CART_ITEM_NAME = ".inventory_item_name"
    REMOVE_BUTTON = "[data-test^='remove-']"
    CONTINUE_SHOPPING_BUTTON = "#continue-shopping"
    CHECKOUT_BUTTON = "#checkout"
    
    def __init__(self, page: Page):
        super().__init__(page)
    
    def is_cart_page_displayed(self) -> bool:
        """Check if cart page is displayed"""
        return self.is_visible(self.CART_CONTAINER)
    
    def get_cart_item_count(self) -> int:
        """Get number of items in cart"""
        return self.get_element_count(self.CART_ITEM)
    
    def remove_item_from_cart(self):
        """Remove first item from cart"""
        if self.is_visible(self.REMOVE_BUTTON):
            self.click(self.REMOVE_BUTTON)
            self.wait(0.5)
    
    def continue_shopping(self):
        """Click continue shopping button"""
        self.click(self.CONTINUE_SHOPPING_BUTTON)
        self.wait(0.5)
    
    def checkout(self):
        """Click checkout button"""
        self.click(self.CHECKOUT_BUTTON)
        self.wait(0.5)
    
    def is_cart_empty(self) -> bool:
        """Check if cart is empty"""
        return self.get_cart_item_count() == 0
