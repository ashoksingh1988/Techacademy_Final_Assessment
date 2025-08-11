from selenium.webdriver.common.by import By
from pages.base_page import BasePage


class LoginPage(BasePage):

    SWAG_LABS_LOGO = (By.CLASS_NAME, "login_logo")
    USERNAME_FIELD = (By.ID, "user-name")
    PASSWORD_FIELD = (By.ID, "password")
    LOGIN_BUTTON = (By.ID, "login-button")
    ERROR_MESSAGE = (By.XPATH, "//h3[@data-test='error']")

    def __init__(self, driver):
        super().__init__(driver)

    def verify_swag_labs_present(self):
        is_present = self.is_element_visible(self.SWAG_LABS_LOGO)
        if is_present:
            logo_text = self.get_text(self.SWAG_LABS_LOGO)
            return True
        return False

    def login(self, username, password):
        self.enter_text(self.USERNAME_FIELD, username)
        self.enter_text(self.PASSWORD_FIELD, password)
        self.click_element(self.LOGIN_BUTTON)

    def is_login_successful(self):
        try:
            return "inventory" in self.driver.current_url
        except:
            return False

    def get_error_message(self):
        try:
            return self.get_text(self.ERROR_MESSAGE)
        except:
            return ""
