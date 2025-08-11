from selenium.webdriver.common.by import By
from pages.base_page import BasePage


class CartPage(BasePage):

    CART_ITEMS = (By.CLASS_NAME, "cart_item")
    CART_ITEM_NAMES = (By.CLASS_NAME, "inventory_item_name")
    CONTINUE_SHOPPING_BUTTON = (By.ID, "continue-shopping")
    CHECKOUT_BUTTON = (By.ID, "checkout")

    def __init__(self, driver):
        super().__init__(driver)

    def verify_item_in_cart(self, expected_item_name):
        try:
            if self.is_element_visible(self.CART_ITEMS):
                cart_item_names = self.driver.find_elements(*self.CART_ITEM_NAMES)
                for item in cart_item_names:
                    if expected_item_name in item.text:
                        return True
                return False
            return False
        except:
            return False

    def get_cart_items_count(self):
        return self.get_element_count(self.CART_ITEMS)
