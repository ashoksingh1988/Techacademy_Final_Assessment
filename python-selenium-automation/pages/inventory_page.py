from selenium.webdriver.common.by import By
from pages.base_page import BasePage


class InventoryPage(BasePage):

    INVENTORY_ITEMS = (By.CLASS_NAME, "inventory_item")
    ADD_TO_CART_BUTTONS = (By.XPATH, "//button[contains(@id, 'add-to-cart')]")
    FIRST_ADD_TO_CART_BUTTON = (By.XPATH, "(//button[contains(@id, 'add-to-cart')])[1]")
    SHOPPING_CART_BADGE = (By.CLASS_NAME, "shopping_cart_badge")
    SHOPPING_CART_LINK = (By.CLASS_NAME, "shopping_cart_link")
    MENU_BUTTON = (By.ID, "react-burger-menu-btn")
    LOGOUT_LINK = (By.ID, "logout_sidebar_link")
    INVENTORY_ITEM_NAMES = (By.CLASS_NAME, "inventory_item_name")
    FIRST_ITEM_NAME = (By.XPATH, "(//div[@class='inventory_item_name '])[1]")

    def __init__(self, driver):
        super().__init__(driver)

    def add_first_item_to_cart(self):
        first_item_name = self.get_text(self.FIRST_ITEM_NAME)
        self.click_element(self.FIRST_ADD_TO_CART_BUTTON)
        return first_item_name
    
    def verify_item_added_to_cart(self):
        try:
            import time
            time.sleep(3)  # Wait for cart to update
            if self.is_element_visible(self.SHOPPING_CART_BADGE):
                cart_count = self.get_text(self.SHOPPING_CART_BADGE)
                return cart_count == "1"
            return False
        except:
            return False

    def click_shopping_cart(self):
        try:
            # Try multiple locators for shopping cart
            cart_locators = [
                (By.CLASS_NAME, "shopping_cart_link"),
                (By.ID, "shopping_cart_container"),
                (By.XPATH, "//a[@class='shopping_cart_link']"),
                (By.XPATH, "//div[@id='shopping_cart_container']")
            ]

            for locator in cart_locators:
                try:
                    self.click_element(locator)
                    import time
                    time.sleep(2)
                    if "cart" in self.driver.current_url:
                        return True
                except:
                    continue
            return False
        except:
            return False

    def open_menu(self):
        self.click_element(self.MENU_BUTTON)

    def logout(self):
        self.open_menu()
        import time
        time.sleep(1)
        self.click_element(self.LOGOUT_LINK)

    def verify_logout_successful(self):
        try:
            current_url = self.driver.current_url
            return "saucedemo.com" in current_url and "inventory" not in current_url
        except:
            return False
